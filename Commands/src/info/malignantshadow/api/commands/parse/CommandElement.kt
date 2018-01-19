package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.CommandParameter

/**
 * Represents a command element. It is a simple pairing between a parameter and its input.
 * It also includes the parsed value.
 *
 * @param key The parameter. Can be null to indicate no parameter is associated with
 * the input
 * @param input The input
 */
data class CommandElement(val key: CommandParameter?, val input: String) {

    /**
     * The parsed value of this element
     */
    val value = key?.getValueFrom(input)

    /**
     * The parsed value of this element
     */
    operator fun component3() = value

}