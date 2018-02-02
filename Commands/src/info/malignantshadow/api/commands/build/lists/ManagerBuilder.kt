package info.malignantshadow.api.commands.build.lists

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.Manager
import info.malignantshadow.api.commands.build.CommandBuilder
import info.malignantshadow.api.commands.dispatch.Context
import info.malignantshadow.api.commands.dispatch.Result
import info.malignantshadow.api.util.build

class ManagerBuilder : CommandDslListBuilder<CommandBuilder, Command>() {

    private var helpFn = Manager.DEFAULT_HELP_FUNCTION
    private var beforeDispatch: (Context) -> Boolean = { _ -> true }
    private var afterDispatch: (Context, Result?) -> Unit = { _, _ -> }
    private var sourceRequirementFallthrough = false
    private var ignoreUnnecessaryFlagInput = false

    override fun createBuilder(name: String): CommandBuilder = CommandBuilder(name, helpFn)

    internal fun build() =
            Manager(
                    list,
                    beforeDispatch,
                    afterDispatch,
                    sourceRequirementFallthrough,
                    ignoreUnnecessaryFlagInput
            )

    fun beforeDispatch(fn: (Context) -> Boolean) {
        beforeDispatch = fn
    }

    fun afterDispatch(fn: (Context, Result?) -> Unit) {
        afterDispatch = fn
    }

    fun sourceRequirementFallsThrough(fallthrough: Boolean = true) {
        sourceRequirementFallthrough = fallthrough
    }

    fun ignoreUnnecessaryFlagInput(ignore: Boolean = true) {
        ignoreUnnecessaryFlagInput = ignore
    }

}

fun commands(init: ManagerBuilder.() -> Unit) = build(ManagerBuilder(), init).build()