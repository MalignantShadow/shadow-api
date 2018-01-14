package info.malignantshadow.api.commands

import info.malignantshadow.api.util.build
import info.malignantshadow.api.util.parsing.Tokenizer

@CommandDsl
abstract class CommandManager<C : Command<C, S>, S : CommandSender> {

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
                            return@handler
                        }

                        it.sender.print("${help.formatFullCommand(fullCmdPath).trim()} ${help.formatSimpleCommand(command)}")

                        command.args.forEach { a ->
                            it.sender.print("  ${help.formatArg(a.shownDisplay, a.isRequired)} ${help.formatDescription(a.desc)}")
                        }
                        return@handler
                    }
                }

                val shownHelp = help.getHelp(page)
                if (shownHelp == null) {
                    it.sender.printErr("Page %d does not exist", page)
                    return@handler
                }

                shownHelp.forEach { s -> it.sender.print(s) }
            }
        }
        add(cmd)
    }

    fun add(command: C) {
        require(_commands.firstOrNull { it.conflictsWith(command) } == null) {
            "Command name/alias conflicts with another command in this manager"
        }
    }

    operator fun get(alias: String): C? = _commands.firstOrNull { it.hasAlias(alias) }

    // separate by strings or
    fun dispatch(sender: S, fullCmd: String) {
        require(Regex("[\\n\\r\\f]") !in fullCmd) { "command string cannot contain newline characters" }
        val tokenizer = Tokenizer(fullCmd)
        tokenizer.addTokenType(Tokenizer.string(), 0)
        tokenizer.addTokenType("\\S+", 1)
        var token = tokenizer.next()
        val parts = ArrayList<String>()
        while (token != null) {
            parts.add(token.match)
            token = tokenizer.next()
        }
        dispatch(sender, parts)
    }

    fun dispatch(sender: S, fullCmd: List<String>) {
        require(!fullCmd.isEmpty()) { "Command cannot be empty" }
        val name = fullCmd[0]
        val cmd = get(name)
        if (cmd == null) {
            sender.printErr("Command %s not found", name)
            return
        }

        val rest = fullCmd.slice(1..fullCmd.lastIndex)
        if (rest.isEmpty() || cmd.commands.isEmpty()) {
            val context = cmd.createContext(name, cmd.getParts(sender, rest) ?: return)

            context.parts.forEach {
                val arg = it.arg
                if (arg != null && arg.isRequired && !arg.nullable && it.value == null) {
                    sender.printErr("Invalid input for argument '%s': \"%s\"", name, arg.shownDisplay, it.input)
                    return@dispatch
                }
            }

            if (commandWillDispatch(context)) {
                try {
                    context.dispatchSelf()
                } catch (e: Exception) {
                    e.printStackTrace()
                    sender.printErr("An error occurred while running this command")
                }
                commandDidDispatch(context)
            }
            return
        }

        cmd.commands.dispatch(sender, rest)
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
