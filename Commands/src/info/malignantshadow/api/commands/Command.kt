package info.malignantshadow.api.commands

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.arguments.Argument
import info.malignantshadow.api.util.arguments.ArgumentHolder
import info.malignantshadow.api.util.arguments.ArgumentList
import info.malignantshadow.api.util.arguments.ParsedArguments

open class Command(
        override val name: String,
        val desc: String = "",
        override val aliases: List<String> = emptyList()
) : Aliasable, ArgumentHolder {

    var arguments: ArgumentList = ArgumentList()
    var handler: ((CommandContext) -> Unit)? = {}
    var hidden: Boolean = false
    var subCommands: CommandManager? = null
    val isParent get() = subCommands?.commands?.isEmpty()?.not() ?: false

    val allAliases: List<String> get() = listOf(name, *aliases.toTypedArray())

    constructor(name: String, desc: String = "", aliases: List<String> = emptyList(), init: Command.() -> Unit) : this(name, desc, aliases) {
        this.init()
    }

    override fun withArgs(args: Iterable<Argument>): Command {
        arguments.withArgs(args)
        return this
    }

    override fun withArg(arg: Argument): Command {
        arguments.withArg(arg)
        return this
    }

    fun withArg(name: String, desc: String?, required: Boolean = false, display: String = "", init: Argument.() -> Unit = {}): Command {
        val arg = Argument(name, desc, required, display)
        arg.init()
        return this
    }

    fun createContext(sender: CommandSender, prefix: String, args: List<String> = listOf()): CommandContext? {
        if(arguments.argsList.size < arguments.min) return null
        return CommandContext(prefix, sender, this, ParsedArguments(arguments, args))
    }

    operator fun contains(alias: String) = alias == name || name in aliases

    data class Info(val full: String, val cmd: Command, val args: List<String>)

}