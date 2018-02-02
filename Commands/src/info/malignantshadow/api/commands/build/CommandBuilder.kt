package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Flag
import info.malignantshadow.api.commands.Parameter
import info.malignantshadow.api.commands.build.attributes.Aliasable
import info.malignantshadow.api.commands.build.attributes.Describable
import info.malignantshadow.api.commands.build.attributes.SimpleAliasable
import info.malignantshadow.api.commands.build.attributes.SimpleDescribable
import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Result
import info.malignantshadow.api.commands.dispatch.Source

class CommandBuilder(
        private val name: String,
        private val helpFn: (Command) -> List<String>,
        private val aliasable: SimpleAliasable = SimpleAliasable(),
        private val describable: SimpleDescribable = SimpleDescribable()
) : CommandDslBuilder<Command>(), Aliasable by aliasable, Describable by describable {

    private var permission = ""
    private var parameters = ArrayList<Parameter>()
    private var minArgs = 0
    private var extraArgUsage: String = ""
    private var flags = ArrayList<Flag>()
    private var children = ArrayList<Command>()
    private var sourceRequirement = Source.Requirements.HAS_PERMISSION
    private var helpFlags = ArrayList<String>()
    private var handler: ((Context) -> Result?)? = null

    init {
        aliasable.checkAlias(name)
    }

    /**
     * Sets the minimum argument count for this command. The command will not run if the argument
     * count is not met
     * @param min The minimum amount of argument this command must take before executing
     */
    fun minimumArgumentCountOf(min: Int) {
        minArgs = min
    }

    /**
     * Sets the permission that the command source must have in order for this command to be executed.
     *
     * Note: Depending on the [Source] and the source requirement for this command, this value may
     * be ignored.
     *
     * @param permission The permission
     */
    fun permission(permission: String) {
        this.permission = permission
    }

    /**
     * Adds a flag to this command.
     *
     * @param flag The pre-built flag
     */
    fun flag(flag: Flag) {
        require(flags.none { it.name == flag.name }) { "Duplicate flag name '${flag.name}'" }
        flags.add(flag)
    }

    /**
     * Adds a flag with the given name to this command.
     *
     * @param name The name of the flag
     * @param init The extension function used to build the flag
     */
    fun flag(name: String, init: FlagBuilder.() -> Unit = {}) {
        flag(info.malignantshadow.api.util.build(FlagBuilder(name), init).build())
    }

    /**
     * Adds multiple flags to this command.
     *
     * @param flags The pre-built flags
     */
    fun flags(flags: Iterable<Flag>) =
            flags.forEach { flag(it) }

    /**
     * Adds a parameter to this command
     *
     * @param parameter The pre-built parameter
     */
    fun parameter(parameter: Parameter) {
        require(flags.none { it.name == name }) { "Duplicate parameter name ${parameter.name}" }
        parameters.add(parameter)
    }

    /**
     * Adds a parameter with the given name to this command.
     *
     * @param name The name of the parameter
     * @param init THe extension function used to build the parameter
     */
    fun parameter(name: String, init: ParameterBuilder.() -> Unit = {}) {
        parameter(info.malignantshadow.api.util.build(ParameterBuilder(name), init).build())
    }

    /**
     * Adds multiple parameters to this command
     *
     * @param parameters The pre-built parameters
     */
    fun parameters(parameters: Iterable<Parameter>) =
            parameters.forEach { parameter(it) }

    /**
     * Sets the usage string for extra arguments passed to this command.
     *
     * @param usage The usage string. Defaults to `"extra.."` in square brackets to indicate that
     * extra arguments are optional
     */
    fun extraArgUsage(usage: String = "[extra..]") {
        extraArgUsage = usage
    }

    /**
     * Sets the requirement for this command to execute with a given source. By default,
     * this command will not run if the source does not have the permission defined by [permission].
     *
     * Function parameters:
     * * [Source] The source of the command
     * * [Command] The command that `source` is attempting to execute
     */
    fun sourceRequirement(requirement: (Source, Command) -> Boolean) {
        sourceRequirement = requirement
    }

    /**
     * Adds help flags to this command. If a flag with any of the given names is found,
     * parsing will cease immediately, and help will be shown for the command, providing
     * the command source meets the command's source requirement.
     *
     * @param names The names of the flags
     */
    fun helpFlags(names: Iterable<String> = listOf("help", "h")) {
        helpFlags.addAll(names)
    }

    /**
     * Sets the handler for this command
     *
     * @param handler The command's handler
     */
    fun handler(handler: ((Context) -> Result?)?) {
        this.handler = handler
    }

    /**
     * Add a child command to this command.
     *
     * @receiver The name of the command
     * @param init The extension function used to build the command.
     */
    infix fun String.has(init: CommandBuilder.() -> Unit): Command {
        val cmd = info.malignantshadow.api.util.build(CommandBuilder(this, helpFn), init).build()
        children.add(cmd)
        return cmd
    }

    override fun build() =
            Command(
                    name,
                    aliasable.aliases,
                    describable.description,
                    permission,
                    parameters,
                    minArgs,
                    extraArgUsage,
                    flags,
                    sourceRequirement,
                    children,
                    handler,
                    helpFlags,
                    helpFn
            )

}