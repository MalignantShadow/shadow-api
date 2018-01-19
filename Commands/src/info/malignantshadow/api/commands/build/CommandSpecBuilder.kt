package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandParameter
import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.util.build

@CommandDsl
class CommandSpecBuilder(val name: String, val desc: String) {

    private val children = ArrayList<CommandSpec>()
    private val aliases = ArrayList<String>()
    private val params = ArrayList<CommandParameter>()
    private var extra: CommandParameter? = null
    private var sendableBy: ((CommandSource) -> Boolean) = { true }
    private var handler: ((CommandContext) -> CommandResult?)? = null
    private var hiddenFor: (CommandSource) -> Boolean = { false }

    private fun conflictsWith(other: CommandSpec): Boolean {
        if(other.hasAlias(name)) return true
        return aliases.firstOrNull { other.hasAlias(it) } != null
    }

    init {
        checkAlias(name)
    }

    fun child(name: String, desc: String, init: CommandSpecBuilder.() -> Unit) : CommandSpec {
        val child = build(CommandSpecBuilder(name, desc), init).build()
        child(child)
        return child
    }

    fun child(child: CommandSpec) {
        require(children.firstOrNull { conflictsWith(it) } == null) {
            "Command already contains a child with one of the aliases of the specified command"
        }
        children.add(child)
    }

    fun children(children: Iterable<CommandSpec>) {
        children.forEach { child(it) }
    }

    fun setChildren(children: Iterable<CommandSpec>) {
        this.children.clear()
        children(children)
    }

    fun param(name: String, desc: String, display: String = "", init: CommandParameterBuilder.() -> Unit) : CommandParameter {
        val param = build(CommandParameterBuilder(name, desc, display), init).build()
        param(param)
        return param
    }

    fun param(param: CommandParameter) {
        require(params.firstOrNull { it.name == param.name } == null) {
            "Command already contains a parameter with the name ${param.name}"
        }
        params.add(param)
    }

    fun params(parameters: Iterable<CommandParameter>) {
        parameters.forEach { param(it) }
    }

    fun setParams(parameters: Iterable<CommandParameter>) {
        params.clear()
        params(parameters)
    }

    fun extra(display: String, desc: String) {
        extra = CommandParameterBuilder(display, desc, "").build()
    }

    fun sendableBy(fn: (CommandSource) -> Boolean) {
        sendableBy = fn
    }

    fun alias(alias: String) {
        require(alias !in aliases) { "Command already has alias '$alias'" }
        aliases.add(alias)
    }

    fun aliases(aliases: Iterable<String>) {
        this.aliases.forEach { alias(it) }
    }

    fun setAliases(aliases: Iterable<String>) {
        this.aliases.clear()
        aliases(aliases)
    }

    fun handler(handler: (CommandContext) -> CommandResult?) {
        this.handler = handler
    }

    fun hiddenFor(fn: (CommandSource) -> Boolean) {
        hiddenFor = fn
    }

    fun isHidden() {
        hiddenFor = { true }
    }

    internal fun build() =
            CommandSpec(name, aliases, desc, params, extra, handler, children, sendableBy, hiddenFor)

}