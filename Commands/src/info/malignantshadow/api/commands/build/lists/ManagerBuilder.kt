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

    /**
     * Sets the function that runs before a command is dispatched. This occurs after the command's
     * [SourceRequirement][info.malignantshadow.api.commands.dispatch.SourceRequirement] is checked.
     *
     * Function parameters:
     * * [Context] - The command context that is passed to the command's handler
     *
     * If the function returns `false`, the command will not be dispatched.
     * By default, all commands are dispatched.
     */
    fun beforeDispatch(fn: (Context) -> Boolean) {
        beforeDispatch = fn
    }

    /**
     * Sets the function that runs after a command has been dispatched (its handler has been invoked).
     * This function will run even if the command fails.
     *
     * Function parameters:
     * * [Context] - The command context that is passed to the command's handler
     * * [Result]? - The result of the command. Note that a `null` result does not necessarily mean the command failed.
     * If an uncaught exception occurred within the command's handler, then the result will be an instance of
     * [DispatchErrorResult][info.malignantshadow.api.commands.Manager.DispatchErrorResult] with a type of
     * [EXCEPTION_DURING_DISPATCH][info.malignantshadow.api.commands.Manager.EXCEPTION_DURING_DISPATCH]
     */
    fun afterDispatch(fn: (Context, Result?) -> Unit) {
        afterDispatch = fn
    }

    /**
     * Sets whether the source requirement of a parent command should be checked if the command source tries to
     * execute one its child commands. The default behavior is `false`, unless this function is invoked.
     *
     * @param fallthrough Whether the source requirement of parent commands should be checked. Defaults to true.
     */
    fun sourceRequirementFallsThrough(fallthrough: Boolean = true) {
        sourceRequirementFallthrough = fallthrough
    }

    /**
     * Sets whether this manager should not cause an error if a flag was given input when said flag does not
     * accept any input at all (i.e. only its presence is required to be meaningful). The default behavior is `false`,
     * unless this function is invoked.
     *
     * Note: The input itself is not actually 'ignored' - it is still mapped as a value for the flag. This indication
     * is simply to prevent an error from occurring.
     *
     * @param ignore Whether to ignore unnecessary flag input
     */
    fun ignoreUnnecessaryFlagInput(ignore: Boolean = true) {
        ignoreUnnecessaryFlagInput = ignore
    }

}

/**
 * Creates a command manager
 *
 * @param init The extension function used to build the [Manager]
 */
fun commands(init: ManagerBuilder.() -> Unit) = build(ManagerBuilder(), init).build()