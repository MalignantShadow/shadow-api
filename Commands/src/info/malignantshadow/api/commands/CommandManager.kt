package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.commands.parse.CommandParseException
import info.malignantshadow.api.commands.parse.CommandParser

class CommandManager(
        val commands: List<CommandSpec>,
        val onSelect: ((CommandSpec) -> Boolean)?,
        val commandWillDispatch: ((CommandContext) -> Boolean)?,
        val commandDidDispatch: ((CommandContext, CommandResult?) -> Unit)?
) {

    companion object {

        const val HELP_SENT = 0
        const val HELP_UNKNOWN_COMMAND = 1
        const val HELP_PAGE_NOT_FOUND = 2

        const val CMD_NOT_FOUND = 0
        const val CMD_REQUIRES_SUB = 1
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

    data class CommandDispatchErrorResult(val source: CommandSource, val key: String, val type: Int) : CommandResult
    data class CommandNotDispatchedResult(val source: CommandSource, val cmd: CommandSpec) : CommandResult
    data class ExceptionInCommandBodyResult(val source: CommandSource, val context: CommandContext, val exception: Exception) : CommandResult
    data class CommandParseErrorResult(val source: CommandSource, val cmd: CommandSpec, val message: String) : CommandResult

    fun getVisibleChildren(source: CommandSource) = commands.filter { !it.isHiddenFor(source) }
    fun getSendableChildren(source: CommandSource) = commands.filter { it.isSendableBy(source) }

    private fun notFound(source: CommandSource, name: String): CommandDispatchErrorResult {
        source.printErr("Command '%s' not found", name)
        return CommandDispatchErrorResult(source, name, CMD_NOT_FOUND)
    }

    private fun requiresSub(source: CommandSource, cmd: CommandSpec) : CommandDispatchErrorResult {
        source.printErr("Command '%s' requires a sub-command", cmd.name)
        return CommandDispatchErrorResult(source, cmd.name, CMD_REQUIRES_SUB)
    }

    fun dispatch(source: CommandSource, command: String): CommandResult? {
        require(!command.isBlank()) { "Command string cannot be blank" }
        val tokenizer = CommandParser.getTokenizer(command)
        var token = tokenizer.next() // There should be a token if the string isn't blank
        if (token!!.type != CommandParser.OTHER) {
            source.printErr("Expected a command name, received: %s", token.match)
            return CommandDispatchErrorResult(source, token.match, CMD_INVALID_INPUT)
        }

        var cmd = commands[token.match] ?: return notFound(source, token.match)
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
        if (onSelect?.invoke(cmd) == false)
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