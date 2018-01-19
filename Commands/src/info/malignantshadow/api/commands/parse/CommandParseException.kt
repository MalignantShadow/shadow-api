package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.CommandSpec

class CommandParseException(val cmd: CommandSpec, override val message: String) : RuntimeException(message)