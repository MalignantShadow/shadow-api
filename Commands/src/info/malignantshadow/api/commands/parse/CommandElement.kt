package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.CommandParameter

data class CommandElement(val key: CommandParameter?, val input: String) {

    val value = key?.getValueFrom(input)

    operator fun component3() = value

}