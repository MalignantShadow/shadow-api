package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandManager
import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.util.build

@CommandDsl
class CommandManagerBuilder {

    private val commands = ArrayList<CommandSpec>()
    private var onSelect: ((CommandSpec) -> Boolean)? = null
    private var commandWillDispatch: ((CommandContext) -> Boolean)? = { true }
    private var commandDidDispatch: ((CommandContext, CommandResult?) -> Unit)? = null

    fun command(name: String, desc: String, init: CommandSpecBuilder.() -> Unit) : CommandSpec {
        val command = build(CommandSpecBuilder(name, desc), init).build()
        commands.add(command)
        return command
    }

    fun onSelect(fn: (CommandSpec) -> Boolean) {
        onSelect = fn
    }

    fun commandWillDispatch(fn: (CommandContext) -> Boolean) {
        commandWillDispatch = fn
    }

    fun commandDidDispatch(fn: (CommandContext, CommandResult?) -> Unit) {
        commandDidDispatch = fn
    }

    internal fun build() : CommandManager = CommandManager(commands, onSelect, commandWillDispatch, commandDidDispatch)
}

fun commandManager(init: CommandManagerBuilder.() -> Unit) : CommandManager =
        build(CommandManagerBuilder(), init).build()