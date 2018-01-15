package info.malignantshadow.api.commands

open class HelpListing(val fullCmd: String, val commands: MutableList<Command<*, *>>) {

    open fun formatFullCommand(fullCmd: String = this.fullCmd): String = fullCmd

    open fun formatArg(arg: String, required: Boolean) =
            (if (required) "<%s>" else "[%s]").format(arg)

    open fun formatArgs(args: Iterable<CommandArgument>): String =
            args.joinToString(" ", transform = { formatArg(it.shownDisplay, it.isRequired) })

    open fun formatAliases(aliases: Iterable<String>) = aliases.joinToString("/")

    open fun formatDescription(desc: String) = if (desc.isEmpty()) "" else "- $desc"

    open fun formatSimpleCommand(cmd: Command<*, *>): String {
        val args = ArrayList(cmd.args)
        if (cmd.extraArg != null) args.add(cmd.extraArg)
        return "${formatAliases(cmd.allAliases)} ${formatArgs(args)} ${formatDescription(cmd.desc)}"
    }

    open fun formatCommandNested(cmd: Command<*, *>): String {
        val dummy = ArrayList<CommandArgument>()
        dummy.add(CommandArgument("command", "The sub-command to run", true))
        return "${formatAliases(cmd.allAliases)} ${formatArgs(dummy)} ${formatDescription(cmd.desc)}"
    }

    open fun getCommandHelp(cmd: Command<*, *>): String = if (cmd.isParent) formatCommandNested(cmd) else formatSimpleCommand(cmd)

    open fun getHelp(page: Int = 1): ArrayList<String>? {
        val help = arrayListOf<String>()
        with(help) {
            add("Usage: $fullCmd <command>")
            add("")
            add("Commands:")
            commands.forEach { add("  ${getCommandHelp(it)}") }
        }
        return help
    }

}