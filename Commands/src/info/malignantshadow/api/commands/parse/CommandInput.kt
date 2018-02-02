package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.Parameter

data class CommandInput(
        val key: Parameter?,
        val input: String?,
        val value: Any? = key?.getValueFrom(input)
)