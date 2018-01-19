package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.commands.parse.CommandParseException
import info.malignantshadow.api.commands.parse.CommandParser

/**
 * Represents a command manager. A command manager is in charge of selecting which command to dispatch
 * when a [CommandSource] runs a command. The selected command and its input are given to a [CommandParser],
 * whose tokens are used to create a context. This context is then given to a handler, who determines the result
 * of the command.
 */
class CommandManager(
        commands: List<CommandSpec>,

        val onSelect: ((CommandSource, CommandSpec) -> Boolean)?,
        val commandWillDispatch: ((CommandContext) -> Boolean)?,
        val commandDidDispatch: ((CommandContext, CommandResult?) -> Unit)?
) : CommandContainer(commands) {

    companion object {

        const val HELP_SENT = 0
        const val HELP_UNKNOWN_COMMAND = 1
        const val HELP_PAGE_NOT_FOUND = 2

        /**
         * Indicates that a command (the key) could not be found.
         */
        const val CMD_NOT_FOUND = 0

        /**
         * Indicates that a command (the key) requires a sub-command, but the source
         * attempted to run the command anyway.
         */
        const val CMD_REQUIRES_SUB = 1

        /**
         * Indicates that a given input (the key) is invalid.
         */
        const val CMD_INVALID_INPUT = 2

    }

    init {
        commands.forEach {
            commands.forEach loop@{ other ->
                if(it === other)
                    return@loop // continue

                it.allAliases.forEach { alias ->
                    check(!other.hasAlias(alias)) { "More than one command with the alias '$alias'" }
                }
            }
        }
    }

    /**
     * Represents that an error occurred sometime in the dispatching process.
     */
    data class CommandDispatchErrorResult(

            /**
             * The source of the command.
             */
            val source: CommandSource,

            /**
             * The key associated with the error. Usually the command's name.
             */
            val key: String,

            /**
             * The error type one of [CMD_NOT_FOUND], [CMD_INVALID_INPUT],
             * or [CMD_REQUIRES_SUB]
             */
            val type: Int
    ) : CommandResult

    /**
     * Represents that a command was not dispatched because either [onSelect] or [commandWillDispatch] prevented it.
     */
    data class CommandNotDispatchedResult(

            /**
             * The source of the command.
             */
            val source: CommandSource,

            /**
             * The command.
             */
            val cmd: CommandSpec
    ) : CommandResult

    /**
     * Represents that an (uncaught) exception occurred in the command's handler.
     */
    data class ExceptionInCommandBodyResult(

            /**
             * The source of the command.
             */
            val source: CommandSource,

            /**
             * The context of the command.
             */
            val context: CommandContext,

            /**
             * The exception that was thrown.
             */
            val exception: Exception
    ) : CommandResult

    /**
     * Represents that a [CommandParseException] was thrown while a command's input was being parsed.
     */
    data class CommandParseErrorResult(

            /**
             * The source of the command.
             */
            val source: CommandSource,

            /**
             * The command.
             */
            val cmd: CommandSpec,

            /**
             * The exception's message
             */
            val message: String
    ) : CommandResult

    private fun notFound(source: CommandSource, name: String): CommandDispatchErrorResult {
        source.printErr("Command '%s' not found", name)
        return CommandDispatchErrorResult(source, name, CMD_NOT_FOUND)
    }

    private fun requiresSub(source: CommandSource, cmd: CommandSpec) : CommandDispatchErrorResult {
        source.printErr("Command '%s' requires a sub-command", cmd.name)
        return CommandDispatchErrorResult(source, cmd.name, CMD_REQUIRES_SUB)
    }

    /**
     * Dispatch a command.
     *
     * @param source The source of the command
     * @param command The command string
     * @return the result of the command, or an appropriate result if an error or exception occurred.
     */
    fun dispatch(source: CommandSource, command: String): CommandResult? {
        require(!command.isBlank()) { "Command string cannot be blank" }
        val tokenizer = CommandParser.getTokenizer(command)
        var token = tokenizer.next() // There should be a token if the string isn't blank
        if (token!!.type != CommandParser.OTHER) {
            source.printErr("Expected a command name, received: %s", token.match)
            return CommandDispatchErrorResult(source, token.match, CMD_INVALID_INPUT)
        }

        var cmd = children[token.match] ?: return notFound(source, token.match)
        while (true) {
            if (!cmd.isParent) // cmd has no children, therefore has a handler
                return dispatch(source, cmd, tokenizer.rest)

            token = tokenizer.next()
            if (token == null || token.type != CommandParser.OTHER)
                return if (cmd.handler == null)
                    requiresSub(source, cmd)
                else
                    dispatch(source, cmd, "${token?.match ?: ""} ${tokenizer.rest}")

            val newCmd = cmd.children[token.match]
            if (newCmd == null)
                return if (cmd.handler == null) {
                    requiresSub(source, cmd)
                } else
                    dispatch(source, cmd, "${token.match} ${tokenizer.rest}")
            else
                cmd = newCmd
        }
    }

    private fun dispatch(source: CommandSource, cmd: CommandSpec, rest: String): CommandResult? {
        if (onSelect?.invoke(source, cmd) == false)
            return CommandNotDispatchedResult(source, cmd)

        val ctx = try {
            CommandContext(cmd, source, CommandParser(cmd, rest).elements)
        } catch (e: CommandParseException) {
            source.print(e.message)
            return CommandParseErrorResult(source, cmd, e.message)
        }

        ctx.args.forEach {
            val param = it.key!!
            if(param.isFlag) {
                val required = param.isRequired && !param.types.isEmpty()
                if(required && !param.isNullable && it.value == null) {
                    source.printErr("Invalid input for flag '%s' - '%s'", param.name.substring(1), it.input)
                    return CommandDispatchErrorResult(source, ctx.cmd.name, CMD_INVALID_INPUT)
                }
            } else if(param.isRequired && !param.isNullable && it.value == null) {
                source.printErr("Invalid input for parameter '%s' - '%s'", param.name, it.input)
                return CommandDispatchErrorResult(source, ctx.cmd.name, CMD_INVALID_INPUT)
            }
        }

        if (commandWillDispatch?.invoke(ctx) == true) {
            return try {
                val result = ctx.cmd.handler!!(ctx)
                commandDidDispatch?.invoke(ctx, result)
                result
            } catch (e: Exception) {
                source.printErr("An error occurred while running this command")
                ExceptionInCommandBodyResult(source, ctx, e)
            }
        }

        return CommandNotDispatchedResult(source, cmd)
    }

    private operator fun List<CommandSpec>.get(alias: String) =
            firstOrNull { it.name.equals(alias, true) }

}