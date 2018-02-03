package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.Command

typealias SourceRequirement = (Source, Command) -> Boolean

/**
 * Represents the source of a command.
 */
open class Source {

    /**
     * Indicates whether this command source has the given permission.
     *
     * @param permission The permission
     */
    open fun hasPermission(permission: String) = true

    /**
     * Prints a message to the command source. The default implementation is to
     * print the message via `System.out`
     *
     * @param message The message
     * @param args Arguments for the message, to be passed to [String.format]
     */
    open fun print(message: String, vararg args: Any?) =
        println(message.format(*args))

    /**
     * Prints an error message to the command source. The default implementation is to
     * print the message via `System.err`
     *
     * @param message The message
     * @param args Arguments for the message, to be passed to [String.format]
     */
    open fun printErr(message: String, vararg args: Any?) =
            System.err.println(message.format(*args))

    /**
     * Cataloged source requirements.
     *
     * @author Shad0w (Caleb Downs)
     */
    object Requirements {

        /**
         * Allows the source to run the command if it has permission to use it.
         */
        val HAS_PERMISSION = { source: Source, cmd: Command -> source.hasPermission(cmd.permission) }

        /**
         * Allows the source to run the command if it meets all of the given requirements.
         *
         * @param requirements The requirements
         */
        fun all(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.all { it(source, cmd) }
        }

        /**
         * Allows the source to run the command if it meets all of the given requirements.
         *
         * @param first The first requirement
         * @param second The second requirement
         * @param others Other requirements
         */
        fun all(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                all(listOf(first, second, *others))

        /**
         * Allows the source to run the command if it meets any one of the given requirements.
         *
         * @param requirements The requirements
         */
        fun any(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.any { it(source, cmd) }
        }

        /**
         * Allows the source to run the command if it meets any one of the given requirements.
         *
         * @param first The first requirement
         * @param second The second requirement
         * @param others Other requirements
         */
        fun any(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                any(listOf(first, second, *others))

        /**
         * Allows the source to run the command if none of the given requirements are met.
         *
         * @param requirements The requirements
         */
        fun none(requirements: Iterable<SourceRequirement>) = { source: Source, cmd: Command ->
            requirements.none { it(source, cmd) }
        }

        /**
         * Allows the source to run the command if none of the given requirements are met.
         *
         * @param first The first requirement
         * @param second The second requirement
         * @param others Other requirements
         */
        fun none(first: SourceRequirement, second: SourceRequirement, vararg others: SourceRequirement) =
                none(listOf(first, second, *others))

    }

}