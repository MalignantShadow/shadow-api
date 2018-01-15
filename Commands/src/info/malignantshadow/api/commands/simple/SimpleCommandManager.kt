package info.malignantshadow.api.commands.simple

import info.malignantshadow.api.commands.CommandContext
import info.malignantshadow.api.commands.CommandManager
import info.malignantshadow.api.commands.CommandSender
import info.malignantshadow.api.util.build

typealias SimpleCommandContext = CommandContext<SimpleCommand, CommandSender>

class SimpleCommandManager : CommandManager<SimpleCommand, CommandSender>() {

    override fun createCommand(name: String, desc: String): SimpleCommand = SimpleCommand(name, desc)

    override fun copy(): SimpleCommandManager {
        val manager = SimpleCommandManager()
        commands.forEach { manager.add(it) }
        return manager
    }

}

fun commandManager(init: SimpleCommandManager.() -> Unit) = build(SimpleCommandManager(), init)