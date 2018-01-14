package info.malignantshadow.api.commands

import info.malignantshadow.api.util.build

@CommandDsl
abstract class Command<C: Command<C, S>, S: CommandSender>(val name: String, val desc: String) {

    private val _aliases = ArrayList<String>()
    private val _args = ArrayList<CommandArgument>()
    private var _extraArg: CommandArgument? = null

    var handler: ((CommandContext<C, S>) -> Unit)? = null
    var isHidden = false

    abstract val commands: CommandManager<C, S>
    val aliases = _aliases.toList()
    val allAliases get() = listOf(name, *aliases.toTypedArray())
    val args get() = _args
    val extraArg get() =_extraArg
    val minArgs get() = args.count { it.isRequired }
    val maxArgs get() = args.size
    val argRange get() = minArgs..maxArgs
    val isParent get() = !commands.isEmpty()

    fun command(name: String, desc: String, init: Command<C, S>.() -> Unit): Command<C, S> = commands.command(name, desc, init)

    fun alias(lazyValue: () -> String) { alias(lazyValue()) }
    fun alias(alias: String) {
        if(alias !in _aliases) _aliases.add(alias)
    }

    fun aliases(lazyValue: () -> Iterable<String>) { aliases(lazyValue()) }
    fun aliases(aliases: Iterable<String>) {
        _aliases.clear()
        aliases.forEach { alias(it) }
    }

    fun arg(name: String, desc: String, required: Boolean = false, init: CommandArgument.() -> Unit) =
            _args.build(CommandArgument(name, desc, required), init)

    fun extra(name: String, desc: String, required: Boolean = false) {
        _extraArg = CommandArgument(name, desc, required)
    }

    fun hasAlias(alias: String) = alias == name || alias in _aliases

    fun conflictsWith(other: Command<*, *>): Boolean {
        if(hasAlias(other.name)) return true
        other.aliases.forEach { if(hasAlias(it)) return@conflictsWith true }
        return false
    }

    fun getParts(sender: S, parts: List<String>): List<Command.Part>? {
        val given = parts.size
        if(given < minArgs) {
            sender.print("Not enough arguments given: expected %d, but received %d", minArgs, given)
            return null
        }

        var optionalLeft = given - minArgs
        var index = 0
        val commandParts = ArrayList<Command.Part>()
        args.forEach {
            when {
                it.isRequired -> commandParts.add(Command.Part(it, parts[index++]))
                optionalLeft > 0 -> {
                    optionalLeft--
                    commandParts.add(Command.Part(it, parts[index++]))
                }
            }
        }

        if(optionalLeft > 0) {
            (index until parts.size).mapTo(commandParts) { Command.Part(_extraArg, parts[it], true) }
        }

        return commandParts
    }

    abstract fun createContext(prefix: String, parts: List<Command.Part>): CommandContext<C, S>

    class Part(val arg: CommandArgument?, val input: String, isExtra: Boolean = false) {

        val value by lazy { arg?.getValueFrom(input) }
        val isExtra = isExtra || arg == null

    }

}