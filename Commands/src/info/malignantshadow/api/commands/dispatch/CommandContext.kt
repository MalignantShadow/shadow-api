package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.parse.CommandElement

class CommandContext(
        val cmd: CommandSpec,
        val source: CommandSource,
        args: List<CommandElement>
) {

    val extra = args.filter { it.key == null }.map { it.input }
    val hasExtra = !extra.isEmpty()
    val args = args.filter { it.key != null }

    @JvmName("isPresent")
    operator fun contains(name: String): Boolean = getElement(name)?.input?.isBlank() == false

    fun getElement(name: String) = args.firstOrNull { it.key?.name == name }

    operator fun get(name: String)= getElement(name)?.value

}