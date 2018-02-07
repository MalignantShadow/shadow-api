package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Manager
import info.malignantshadow.api.commands.Option
import info.malignantshadow.api.commands.Parameter
import info.malignantshadow.api.commands.build.attributes.Aliasable
import info.malignantshadow.api.commands.build.attributes.Describable
import info.malignantshadow.api.commands.build.attributes.SimpleAliasable
import info.malignantshadow.api.commands.build.attributes.SimpleDescribable
import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Source
import info.malignantshadow.api.util.parsing.ParameterType

class CommandBuilder(
        private val name: String,
        private val defaultHelpFn: (Source, Command) -> List<String>,
        private val defaultOperandRelation: Int,
        private val aliasable: SimpleAliasable = SimpleAliasable(),
        private val describable: SimpleDescribable = SimpleDescribable()
) : CommandDslBuilder<Command>(), Aliasable by aliasable, Describable by describable {

    private var permission = ""
    private var parameters = ArrayList<Parameter>()
    private var minArgs = 0
    private var extraArgUsage: String = ""
    private var flags = ArrayList<Option>()
    private var children = ArrayList<Command>()
    private var sourceRequirement = Source.Requirements.HAS_PERMISSION
    private var helpFlags = ArrayList<String>()
    private var handler: ((Context) -> Any?)? = null
    private var helpFn = defaultHelpFn
    private var operandRelation = defaultOperandRelation

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
     * Adds a option to this command.
     *
     * @param option The pre-built option
     */
    fun option(option: Option) {
        require(flags.none { it.name == option.name }) { "Duplicate option name '${option.name}'" }
        flags.add(option)
    }

    fun option(name: String, type: ParameterType<*>) {
        option(name) {
            type(type)
        }
    }

    /**
     * Adds a option with the given name to this command.
     *
     * @param name The name of the option
     * @param init The extension function used to build the flag
     */
    fun option(name: String, init: OptionBuilder.() -> Unit = {}) {
        option(info.malignantshadow.api.util.build(OptionBuilder(name), init).build())
    }

    /**
     * Adds multiple options to this command.
     *
     * @param flags The pre-built options
     */
    fun options(flags: Iterable<Option>) =
            flags.forEach { option(it) }

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
     * If no function is given, the type of the parameter is implied to be
     * [STRING][info.malignantshadow.api.util.parsing.ParameterTypes.STRING].
     *
     * @param name The name of the parameter
     * @param init The extension function used to build the parameter
     */
    fun parameter(name: String, init: ParameterBuilder.() -> Unit = {}) {
        parameter(info.malignantshadow.api.util.build(ParameterBuilder(name), init).build())
    }

    /**
     * Adds a parameter to this command with the given name that should be parsed as the given type.
     *
     * @param name The name of the parameter
     * @param type How the parameter should be parsed
     */
    fun parameter(name: String, type: ParameterType<*>) {
        parameter(name) {
            type(type)
        }
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
     * Adds help options to this command. If an option with any of the given names is found,
     * parsing will cease immediately, and help will be shown for the command, providing
     * the command source meets the command's source requirement.
     *
     * @param names The names of the options
     */
    fun helpOptions(names: Iterable<String> = listOf("help", "h")) {
        helpFlags.addAll(names)
    }

    /**
     * Sets the handler for this command
     *
     * @param handler The command's handler
     */
    fun handler(handler: ((Context) -> Any?)?) {
        this.handler = handler
    }

    /**
     * Specifies the help function for this command. The given function is not
     * passed down to children.
     */
    fun helpFn(helpFn: (Source, Command) -> List<String>) {
        this.helpFn = helpFn
    }

    /**
     * Specifies that positional parameters and options are mixed
     */
    fun operandsMixed() {
        operandRelation = Manager.OPERANDS_MIXED
    }

    /**
     * Specifies that positional parameters must precede options. If an argument is found
     * that cannot be mapped to a flag, an error will occur.
     */
    fun operandsFirst() {
        operandRelation = Manager.OPERANDS_FIRST
    }


    /**
     * Specifies that options must precede positional parameters (POSIX-ly correct behavior).
     */
    fun operandsLast() {
        operandRelation = Manager.OPERANDS_LAST
    }

    /**
     * Add a child command to this command.
     *
     * @receiver The name of the command
     * @param init The extension function used to build the command.
     */
    infix fun String.has(init: CommandBuilder.() -> Unit): Command {
        val cmd = info.malignantshadow.api.util.build(CommandBuilder(this, defaultHelpFn, defaultOperandRelation), init).build()
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
                    operandRelation,
                    sourceRequirement,
                    children,
                    handler,
                    helpFlags,
                    helpFn
            )

}