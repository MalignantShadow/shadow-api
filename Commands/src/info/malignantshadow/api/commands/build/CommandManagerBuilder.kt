package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandManager
import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource
import info.malignantshadow.api.util.build

/**
 * A builder for command managers.
 */
@CommandDsl
class CommandManagerBuilder {

    private val commands = ArrayList<CommandSpec>()
    private var onSelect: ((CommandSource, CommandSpec) -> Boolean)? = null
    private var commandWillDispatch: ((CommandContext) -> Boolean)? = { true }
    private var commandDidDispatch: ((CommandContext, CommandResult?) -> Unit)? = null
    private var helpFn: ((CommandSource, CommandSpec) -> List<String>)? = null

    /**
     * Add a command to the manager.
     *
     * @param name The command's name
     * @param desc The command's description
     */
    fun command(name: String, desc: String, init: CommandSpecBuilder.() -> Unit): CommandSpec {
        val command =
                if (helpFn != null)
                    build(CommandSpecBuilder(name, desc, helpFn!!), init).build()
                else
                    build(CommandSpecBuilder(name, desc), init).build()
        commands.add(command)
        return command
    }

    fun helpFn(fn: (CommandSource, CommandSpec) -> List<String>) {
        helpFn = fn
    }

    /**
     * Set the function used to determine whether a command should run when a command is selected.
     * * [CommandSource] - The source of the command
     * * [CommandSpec] - The command that was selected
     * * returns [Boolean] - Whether the command should run
     *
     * @param fn The function
     */
    fun onSelect(fn: (CommandSource, CommandSpec) -> Boolean) {
        onSelect = fn
    }

    /**
     * Set the function used to determine whether a command should dispatch *after* it has been selected.
     * * [CommandContext] - The context of the command, including the sender and parsed arguments
     * * returns [Boolean] - Whether the command should run.
     *
     * @param fn The function
     */
    fun commandWillDispatch(fn: (CommandContext) -> Boolean) {
        commandWillDispatch = fn
    }

    /**
     * Set the function that is run after a command has been fully dispatched.
     * * [CommandContext] - The context of the command, including the sender and parsed arguments
     * * [CommandResult] - The result of the command
     *
     * @param fn The function
     */
    fun commandDidDispatch(fn: (CommandContext, CommandResult?) -> Unit) {
        commandDidDispatch = fn
    }

    internal fun build(): CommandManager
            = CommandManager(commands, onSelect, commandWillDispatch, commandDidDispatch)
}

/**
 * Build a CommandManager object.
 */
fun commandManager(init: CommandManagerBuilder.() -> Unit): CommandManager =
        build(CommandManagerBuilder(), init).build()