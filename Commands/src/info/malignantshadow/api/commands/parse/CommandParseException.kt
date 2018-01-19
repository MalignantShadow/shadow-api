package info.malignantshadow.api.commands.parse

import info.malignantshadow.api.commands.CommandSpec

/**
 * Represents an exception that occurs while parsing command input
 *
 * @param cmd The command
 * @param message The message
 */
class CommandParseException(

        /**
         * The command
         */
        val cmd: CommandSpec,

        /**
         * The message
         */
        override val message: String
) : RuntimeException(message)