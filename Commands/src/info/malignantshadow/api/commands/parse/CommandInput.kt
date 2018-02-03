package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.Parameter

/**
 * Represents an argument supplied to a command.
 *
 * @author Shad0w (Caleb Downs)
 */
data class CommandInput(

        /**
         * The parameter or flag that this input and value are associated with
         */
        val key: Parameter?,

        /**
         * The input string argument
         */
        val input: String?,

        /**
         * The parsed value
         */
        val value: Any? = key?.getValueFrom(input)
)