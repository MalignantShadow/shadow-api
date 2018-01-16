package info.malignantshadow.api.commands

import info.malignantshadow.api.util.build

@CommandDsl
abstract class Command<C : Command<C, S>, S : CommandSender>(val name: String, val desc: String) {

    private val _aliases = ArrayList<String>()
    private val _params = ArrayList<CommandParameter>()
    private var _extraParam: CommandParameter? = null

    abstract val subManager: CommandManager<C, S>

    val minArgs get() = params.count { it.isRequired }
    val maxArgs get() = params.size
    val argRange get() = minArgs..maxArgs
    val isParent get() = !subManager.isEmpty()

    val aliases get() = _aliases.toList()
    val allAliases get() = listOf(name, *_aliases.toTypedArray())
        val params: List<CommandParameter> get() = _params.toList()
        val extra get() = _extraParam

    private var isHiddenFn: ((S) -> Boolean) = { false }
    var handler: ((CommandContext<C, S>) -> CommandResult?)? = null

    fun command(name: String, desc: String, init: Command<C, S>.() -> Unit) = subManager.command(name, desc, init)

    fun alias(lazyValue: () -> String) {
        alias(lazyValue())
    }

    fun alias(alias: String) {
        if (alias !in _aliases) _aliases.add(alias)
    }

    fun isHidden() {
        isHiddenFn = { true }
    }

    fun hiddenFor(lazy: ((S) -> Boolean)) {
        isHiddenFn = lazy
    }

    fun isHiddenFor(sender: S): Boolean {
        return isHiddenFn(sender)
    }

    fun aliases(lazyValue: () -> Iterable<String>) {
        aliases(lazyValue())
    }

    fun setAliases(aliases: Iterable<String>) {
        _aliases.clear()
        aliases(aliases)
    }

    fun aliases(aliases: Iterable<String>) {
        aliases.forEach { alias(it) }
    }

    fun param(name: String, desc: String, required: Boolean = false, init: CommandParameter.() -> Unit) =
            _params.build(CommandParameter(name, desc, required), init)

    fun setParams(params: Iterable<CommandParameter>) {
        _params.clear()
        params(params)
    }

    fun params(params: Iterable<CommandParameter>) {
        _params.addAll(params)
    }

    fun extra(name: String, desc: String, required: Boolean = false) {
        _extraParam = CommandParameter(name, desc, required)
    }

    fun hasAlias(alias: String) = alias == name || alias in _aliases

    fun conflictsWith(other: Command<*, *>): Boolean {
        other.allAliases.forEach { if (hasAlias(it)) return@conflictsWith true }
        return false
    }

    fun getParts(sender: S, parts: List<String>): List<Command.Part>? {
        val given = parts.size
        if (given < minArgs) {
            sender.print("Not enough arguments given: expected %d, but received %d", minArgs, given)
            return null
        }

        var optionalLeft = given - minArgs
        var index = 0
        val commandParts = ArrayList<Command.Part>()
        params.forEach {
            when {
                it.isRequired -> commandParts.add(Command.Part(it, parts[index++]))
                optionalLeft > 0 -> {
                    optionalLeft--
                    commandParts.add(Command.Part(it, parts[index++]))
                }
            }
        }

        if (optionalLeft > 0) {
            (index until parts.size).mapTo(commandParts) { Command.Part(_extraParam, parts[it], true) }
        }

        return commandParts
    }

    fun helpCommand(name: String = "help", aliases: List<String> = listOf("?")) = subManager.helpCommand(name, aliases)

    abstract fun createContext(prefix: String, sender: S, parts: List<Command.Part>): CommandContext<C, S>

    class Part(val arg: CommandParameter?, val input: String, isExtra: Boolean = false) {

        val value by lazy { arg?.getValueFrom(input) }
        val isExtra = isExtra || arg == null

    }

}