package info.malignantshadow.api.commands

interface CommandResult

data class BooleanResult(val result: Boolean) : CommandResult
data class NumberResult(val result: Number) : CommandResult
data class StringResult(val result: String) : CommandResult
data class TypeResult(val result: Int) : CommandResult
data class CommandError(val ctx: CommandContext<*, *>, val message: String = "") : CommandResult