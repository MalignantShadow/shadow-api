package info.malignantshadow.api.commands

import info.malignantshadow.api.util.arguments.Argument
import info.malignantshadow.api.util.arguments.ArgumentTypes

open class CommandManager(val commands: ArrayList<Command> = arrayListOf()) {

    constructor(commands: ArrayList<Command> = arrayListOf(), init: CommandManager.() -> Unit) : this(commands) {
        this.init()
    }

    operator fun contains(alias: String) = get(alias) != null

    operator fun get(alias: String): Command? = commands.firstOrNull { alias in it }

    open fun push(cmd: Command): CommandManager {
        cmd.aliases.forEach { require(it !in this) { "A command with the name of $it already exists" } }
        commands.add(cmd)
        return this
    }

    operator fun plusAssign(cmd: Command) {
        push(cmd)
    }

    fun getCommandInfo(fullCommand: String) = getCommandInfo(fullCommand.split("\\s+"))
    fun getCommandInfo(args: List<String>): Command.Info? {
        if (args.isEmpty()) return null

        val command = args[0]
        return getCommandInfo(command, args.slice(1..args.lastIndex))
    }

    fun getCommandInfo(command: String, args: List<String>): Command.Info? {
        var cmd = get(command) ?: return null

        var nested = cmd.subCommands
        var fullPath = command
        var contextArgs = args
        for (i in 0..args.lastIndex) {
            if (nested == null) break
            val label = contextArgs[0]
            val tmpCmd = nested[label] ?: break
            cmd = tmpCmd
            fullPath += " $label"
            contextArgs = contextArgs.slice(1..contextArgs.lastIndex)
            nested = cmd.subCommands
        }

        return Command.Info(fullPath, cmd, contextArgs)
    }

    fun dispatch(sender: CommandSender, fullCommand: String) = dispatch(sender, fullCommand.split("\\s+"))
    fun dispatch(sender: CommandSender, args: List<String>): Boolean {
        val command = args.getOrNull(0) ?: return false
        return dispatch(sender, command, args.slice(1..args.lastIndex))
    }

    fun dispatch(sender: CommandSender, command: String, args: List<String>): Boolean {
        val info = getCommandInfo(command, args)
        if (info == null) {
            sender.printErr("[CommandErr] <%s> - Not found", command)
            return false
        }

        return dispatch(sender, info.cmd, info.full, info.args)
    }

    fun dispatch(sender: CommandSender, cmd: Command, prefix: String, args: List<String>): Boolean {
        val context = createContext(sender, cmd, prefix, args)
        // contextWasCreated?
        if (context == null) {
            sender.printErr("[CommandErr] '%s' - Expected at least %d argument(s), but got %d", prefix, cmd.arguments.min, args.size)
            return false
        }

        context.parsedArgs.forEach {
            val arg = it.arg
            if (arg.required && !arg.canBeNull && it.value == null) {
                sender.printErr("[CommandErr] '%s' - Invalid input for argument '%s': \"%s\"", prefix, arg.display, it.input)
                return@dispatch false
            }
        }

        if (commandWillDispatch(cmd, context)) {
            try {
                if (!context.dispatchSelf()) return false
            } catch (e: Exception) {
                e.printStackTrace()

                context.sender?.printErr("An error occurred while running this command")
                return false
            }

            commandDidDispatch(cmd, context)
            return true
        }
        return false
    }

    fun createContext(sender: CommandSender, command: String, args: List<String>): CommandContext? {
        return createContext(sender, get(command) ?: return null, command, args)
    }

    open fun createContext(sender: CommandSender, cmd: Command, prefix: String, args: List<String>) =
            cmd.createContext(sender, prefix, args)

    fun withHelpCommand(name: String = "help", aliases: List<String> = emptyList()): CommandManager {
        return push(Command(name, "View help", aliases) {
            withArg(Argument("arg", "page | command", false, "The page to view or command to get help for") {
                +arrayOf<(String?) -> Any?>(ArgumentTypes.NUMBER, ArgumentTypes.STRING)
            })
            handler = handler@{
                if(it.sender == null) return@handler
                val split = it.prefix.split("\\s+")
                val fullCmdPath = if (split.isEmpty()) "" else split.slice(0 until split.lastIndex).joinToString(" ")
                var page = 1
                val help = getHelpListing(fullCmdPath, it.sender)
                val arg = it["arg"]
                if(arg != null) {
                    if(arg is Number) page = arg.toInt()
                    else {
                        val name = arg as String
                        val command = get(name)
                        if(command == null) {
                            it.sender.printErr("Sub-command with the name/alias '%s' does not exist", name)
                            return@handler
                        }

                        it.sender.print("${help.formatFullCommand(fullCmdPath).trim()} ${help.formatSimpleCommand(command)}")

                        command.arguments.forEach { arg ->
                            it.sender.print("  ${help.formatArg(arg.shownDisplay, arg.required)} ${help.formatDescription(arg.desc ?: "")}")
                        }
                        return@handler
                    }
                }

                val shownHelp = help.getHelp(page)
                if(shownHelp == null) {
                    it.sender.printErr("Page %d does not exist", page)
                    return@handler
                }

                shownHelp.forEach { s -> it.sender.print(s) }
            }
        })
    }

    open fun getHelpListing(fullCmd: String, sender: CommandSender?) = HelpListing(fullCmd, getVisible(sender).toMutableList())

    open fun getVisible(sender: CommandSender?) = commands.filter { !it.hidden }

    operator fun Command.unaryPlus() = plusAssign(this)

    open fun commandWillDispatch(cmd: Command, context: CommandContext) = true
    open fun commandDidDispatch(cmd: Command, context: CommandContext) = true

    fun sort(sortFn: Comparator<Command> = Comparator { a: Command, b: Command -> a.name.compareTo(b.name) }): CommandManager {
        commands.sortWith(sortFn)
        return this
    }

}