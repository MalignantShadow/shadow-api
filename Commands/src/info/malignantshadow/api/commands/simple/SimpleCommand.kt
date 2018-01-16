package info.malignantshadow.api.commands.simple

import info.malignantshadow.api.commands.Command
import info.malignantshadow.api.commands.CommandContext
import info.malignantshadow.api.commands.CommandSender

class SimpleCommand(name: String, desc: String) : Command<SimpleCommand, CommandSender>(name, desc) {

    override val subManager = SimpleCommandManager()

    override fun createContext(prefix: String, sender: CommandSender, parts: List<Command.Part>): CommandContext<SimpleCommand, CommandSender> =
            CommandContext(prefix, this, sender, parts)

}