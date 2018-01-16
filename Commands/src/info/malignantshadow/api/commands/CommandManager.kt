package info.malignantshadow.api.commands

import info.malignantshadow.api.util.build
import info.malignantshadow.api.util.parsing.Tokenizer

@CommandDsl
abstract class CommandManager<C : Command<C, S>, S : CommandSender> {

    companion object {
        const val HELP_SENT = 0
        const val HELP_UNKNOWN_COMMAND = 1
        const val HELP_PAGE_NOT_FOUND = 2

        const val CMD_NOT_FOUND = 0
        const val CMD_REQUIRES_SUB = 1
        const val CMD_INVALID_INPUT = 2
        const val CMD_EXCEPTION_IN_BODY = 3
        const val CMD_NOT_DISPATCHED = 4
    }

    data class HelpResult(val result: Int, val data: Any? = null) : CommandResult

    inner class CommandDispatchErrorResult(sender: S, cmd: String, args: List<String>, error: Int) : CommandResult

    private val _commands = ArrayList<C>()
    protected abstract fun createCommand(name: String, desc: String): C

    val commands get() = _commands.toList()
    val size = _commands.size

    fun isEmpty() = _commands.isEmpty()

    fun command(name: String, desc: String, init: C.() -> Unit): C {
        val cmd = build(createCommand(name, desc), init)
        add(cmd)
        return cmd
    }

    fun helpCommand(name: String = "help", aliases: List<String> = listOf("?")) {
        val cmd = build(createCommand(name, "View help")) {
            aliases(aliases)
            handler = handler@ {
                val split = it.prefix.split("\\s+")
                val fullCmdPath = if (split.isEmpty()) "" else split.slice(0 until split.lastIndex).joinToString(" ")
                var page = 1
                val help = this@CommandManager.getHelpListing(fullCmdPath, it.sender)
                val arg = it["arg"]
                if (arg != null) {
                    if (arg is Number) page = arg.toInt()
                    else {
                        val cmdName = arg as String
                        val command = this@CommandManager[cmdName]
                        if (command == null) {
                            it.sender.printErr("Sub-command with the name/alias '%s' does not exist", cmdName)
                            return@handler HelpResult(HELP_UNKNOWN_COMMAND, cmdName)
                        }

                        it.sender.print("${help.formatFullCommand(fullCmdPath).trim()} ${help.formatSimpleCommand(command)}")

                        command.params.forEach { a ->
                            it.sender.print("  ${help.formatArg(a.shownDisplay, a.isRequired)} ${help.formatDescription(a.desc)}")
                        }
                        return@handler HelpResult(HELP_SENT)
                    }
                }

                val shownHelp = help.getHelp(page)
                if (shownHelp == null) {
                    it.sender.printErr("Page %d does not exist", page)
                    return@handler HelpResult(HELP_PAGE_NOT_FOUND, page)
                }

                shownHelp.forEach { s -> it.sender.print(s) }
                return@handler HelpResult(HELP_SENT)
            }
        }
        add(cmd)
    }

    fun add(command: C) { _commands.add(command) }

    operator fun get(alias: String): C? = _commands.firstOrNull { it.hasAlias(alias) }

    fun dispatch(sender: S, fullCmd: String): CommandResult? {
        require(Regex("[\\n\\r\\f]") !in fullCmd) { "command string cannot contain newline characters" }
        val tokenizer = Tokenizer(fullCmd)
        tokenizer.addTokenType(Tokenizer.string(), 0)
        tokenizer.addTokenType("\\S+", 1)
        var token = tokenizer.next()
        val parts = ArrayList<String>()
        while (token != null) {
            parts.add(if(token.type == 0) token.match.substring(1..token.match.length) else token.match)
            token = tokenizer.next()
        }
        return dispatch(sender, parts)
    }

    fun dispatch(sender: S, fullCmd: List<String>): CommandResult? {
        require(!fullCmd.isEmpty()) { "Command cannot be empty" }
        val name = fullCmd[0]
        val cmd = get(name)
        val rest = fullCmd.slice(1..fullCmd.lastIndex)
        if (cmd == null) {
            sender.printErr("Command '%s' not found", name)
            return CommandDispatchErrorResult(sender, name, rest, CMD_NOT_FOUND)
        }

        if(rest.isEmpty() && !cmd.isParent && cmd.handler == null) {
            sender.printErr("Command '%s' requires a sub-command", name)
            return CommandDispatchErrorResult(sender, name, rest, CMD_REQUIRES_SUB)
        }

        if (rest.isEmpty() || cmd.commands.isEmpty()) {
            val context = cmd.createContext(name, cmd.getParts(sender, rest) ?: return null)

            context.parts.forEach {
                val arg = it.arg
                if (arg != null && arg.isRequired && !arg.isNullable && it.value == null) {
                    sender.printErr("Invalid input for argument '%s': \"%s\"", arg.shownDisplay, it.input)
                    return@dispatch CommandDispatchErrorResult(sender, name, rest, CMD_INVALID_INPUT)
                }
            }

            if (commandWillDispatch(context)) {
                var result: CommandResult?
                result = try {
                    context.dispatchSelf()
                } catch (e: Exception) {
                    e.printStackTrace()
                    sender.printErr("An error occurred while running this command")
                    CommandDispatchErrorResult(sender, name, rest, CMD_EXCEPTION_IN_BODY)
                }
                commandDidDispatch(context)
                return result
            }
            return CommandDispatchErrorResult(sender, name, rest, CMD_NOT_DISPATCHED)
        }

        return cmd.commands.dispatch(sender, rest)
    }

    open fun getVisible(sender: CommandSender?): List<C> {
        val filter = sender == null
        return commands.filter { filter || !it.isHidden }
    }

    open fun getHelpListing(fullCmd: String, sender: CommandSender?) = HelpListing(fullCmd, getVisible(sender).toMutableList())

    fun commandWillDispatch(context: CommandContext<C, S>) = true
    fun commandDidDispatch(context: CommandContext<C, S>) {}

    operator fun contains(alias: String) = get(alias) != null

    abstract fun copy(): CommandManager<C, S>
}
