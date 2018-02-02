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

    fun minimumArgumentCountOf(min: Int) {
        minArgs = min
    }

    fun permission(permission: String) {
        this.permission = permission
    }

    fun flag(flag: Flag) {
        require(flags.none { it.name == flag.name }) { "Duplicate flag name '${flag.name}'" }
        flags.add(flag)
    }

    fun flag(name: String, init: FlagBuilder.() -> Unit = {}) {
        flag(info.malignantshadow.api.util.build(FlagBuilder(name), init).build())
    }

    fun flags(flags: Iterable<Flag>) =
            flags.forEach { flag(it) }

    fun parameter(parameter: Parameter) {
        require(flags.none { it.name == name }) { "Duplicate parameter name ${parameter.name}" }
        parameters.add(parameter)
    }

    fun parameter(name: String, init: ParameterBuilder.() -> Unit = {}) {
        parameter(info.malignantshadow.api.util.build(ParameterBuilder(name), init).build())
    }

    fun parameters(parameters: Iterable<Parameter>) =
            parameters.forEach { parameter(it) }

    fun extraArgUsage(usage: String = "[extra..]") {
        extraArgUsage = usage
    }

    fun sourceRequirement(requirement: (Source, Command) -> Boolean) {
        sourceRequirement = requirement
    }

    fun helpFlags(names: Iterable<String> = listOf("help", "h")) {
        helpFlags.addAll(names)
    }

    fun handler(handler: ((Context) -> Result?)?) {
        this.handler = handler
    }

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