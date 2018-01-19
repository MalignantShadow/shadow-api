package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandParameter
import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.util.build

@CommandDsl
class CommandSpecBuilder(

        /**
         * The name
         */
        val name: String,

        /**
         * The description
         */
        val desc: String
) {

    private val children = ArrayList<CommandSpec>()
    private val aliases = ArrayList<String>()
    private val params = ArrayList<CommandParameter>()
    private var extra: CommandParameter? = null
    private var sendableBy: ((CommandSource) -> Boolean) = { true }
    private var handler: ((CommandContext) -> CommandResult?)? = null
    private var hiddenFor: (CommandSource) -> Boolean = { false }

    private fun conflictsWith(other: CommandSpec): Boolean {
        if (other.hasAlias(name)) return true
        return aliases.firstOrNull { other.hasAlias(it) } != null
    }

    /**
     * Add a child command.
     *
     * @param name The name of the command
     * @param desc The description of the command
     *
     * @see CommandSpec.name
     * @see CommandSpec.desc
     */
    fun child(name: String, desc: String, init: CommandSpecBuilder.() -> Unit) : CommandSpec {
        val child = build(CommandSpecBuilder(name, desc), init).build()
        child(child)
        return child
    }

    /**
     * Add a child command.
     *
     * @param child The pre-built child command
     */
    fun child(child: CommandSpec) {
        require(children.firstOrNull { conflictsWith(it) } == null) {
            "Command already contains a child with one of the aliases of the specified command"
        }
        children.add(child)
    }

    /**
     * Add multiple child commands
     *
     * @param children The pre-built commands
     */
    fun children(children: Iterable<CommandSpec>) {
        children.forEach { child(it) }
    }

    /**
     * Add multiple child commands
     *
     * @param children The pre-built commands
     */
    fun children(vararg children: CommandSpec) = children(listOf(*children))

    /**
     * Set the children of this command by clearing any previously added children and adding
     * the supplied commands
     *
     * @param children The pre-built children
     */
    fun setChildren(children: Iterable<CommandSpec>) {
        this.children.clear()
        children(children)
    }

    /**
     * Set the children of this command by clearing any previously added children and adding
     * the supplied commands
     *
     * @param children The pre-built children
     */
    fun setChildren(vararg children: CommandSpec) = setChildren(listOf(*children))

    /**
     * Add a parameter to this command.
     *
     * @param name The name of the parameter
     * @param desc The description of the parameter
     * @param display What should be displayed instead of the parameter's name in help listings
     *
     * @see CommandParameter.name
     * @see CommandParameter.desc
     * @see CommandParameter.display
     */
    fun param(name: String, desc: String, display: String = "", init: CommandParameterBuilder.() -> Unit): CommandParameter {
        val param = build(CommandParameterBuilder(name, desc, display), init).build()
        param(param)
        return param
    }

    /**
     * Add a parameter to this command.
     *
     * @param param The pre-built parameter
     */
    fun param(param: CommandParameter) {
        require(params.firstOrNull { it.name == param.name } == null) {
            "Command already contains a parameter with the name ${param.name}"
        }
        params.add(param)
    }

    /**
     * Add multiple parameters to this command.
     *
     * @param parameters The pre-built parameters
     */
    fun params(parameters: Iterable<CommandParameter>) {
        parameters.forEach { param(it) }
    }

    /**
     * Add multiple parameters to this command.
     *
     * @param parameters The pre-built parameters
     */
    fun params(vararg parameters: CommandParameter) = params(listOf(*parameters))

    /**
     * Set the parameters of the command by clearing any previously added parameters
     * and adding the supplied parameters.
     *
     * @param parameters The new, pre-built parameters
     */
    fun setParams(parameters: Iterable<CommandParameter>) {
        params.clear()
        params(parameters)
    }

    /**
     * Set the parameters of the command by clearing any previously added parameters
     * and adding the supplied parameters.
     *
     * @param parameters The new, pre-built parameters
     */
    fun setParams(vararg parameters: CommandParameter) = setParams(listOf(*parameters))

    /**
     * Set the display and description of the 'extra' parameter. Used solely for
     * help listing purposes. Useful for messaging commands
     *
     * @param display The display
     * @param desc The description
     * @param required Whether extra arguments are required. Default is `false`
     *
     * @see CommandSpec.extra
     */
    fun extra(display: String, desc: String, required: Boolean = false) {
        extra = build(CommandParameterBuilder(display, desc, "")) {
            isRequired(required)
        }.build()
    }

    /**
     * Set the function used to determine whether a command can be run by a [CommandSource].
     * By default, the command can be run via any source.
     *
     * @param fn The function
     */
    fun sendableBy(fn: (CommandSource) -> Boolean) {
        sendableBy = fn
    }

    /**
     * Add an alias to this command.
     *
     * @param alias The alias
     */
    fun alias(alias: String) {
        require(alias !in aliases) { "Command already has alias '$alias'" }
        aliases.add(alias)
    }

    /**
     * Add multiple aliases to this command.
     *
     * @param aliases The aliases
     */
    fun aliases(aliases: Iterable<String>) {
        this.aliases.forEach { alias(it) }
    }

    /**
     * Add multiple aliases to this command.
     *
     * @param aliases The aliases
     */
    fun aliases(vararg aliases: String) = aliases(listOf(*aliases))

    /**
     * Set the aliases of this command by clearing all previously added aliases
     * and adding the supplied aliases
     *
     * @param aliases The aliases
     */
    fun setAliases(aliases: Iterable<String>) {
        this.aliases.clear()
        aliases(aliases)
    }

    /**
     * Set the aliases of this command by clearing all previously added aliases
     * and adding the supplied aliases
     *
     * @param aliases The aliases
     */
    fun setAliases(vararg aliases: String) = setAliases(listOf(*aliases))

    /**
     * Set the handler of this command
     * * [CommandContext] - The context in which the command was dispatched
     * * returns [CommandResult]? - The result of the command
     *
     * @param handler The handler
     */
    fun handler(handler: (CommandContext) -> CommandResult?) {
        this.handler = handler
    }

    /**
     * Set the function that determines whether this command should be hidden from a
     * [CommandSource] in help listings. By default, a command is not hidden.
     *
     * This is a *hint*.
     */
    fun hiddenFor(fn: (CommandSource) -> Boolean) {
        hiddenFor = fn
    }

    /**
     * Specify that this command should be hidden in help listings.
     *
     * This is a *hint*.
     */
    fun isHidden() {
        hiddenFor = { true }
    }

    internal fun build() =
            CommandSpec(name, aliases, desc, params, extra, handler, children, sendableBy, hiddenFor)

}