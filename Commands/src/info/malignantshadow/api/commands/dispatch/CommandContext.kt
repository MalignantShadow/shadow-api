package info.malignantshadow.api.commands.dispatch

import info.malignantshadow.api.commands.CommandSpec
import info.malignantshadow.api.commands.parse.CommandElement

/**
 * Represents the context in which a command was dispatched.
 *
 * @constructor Create a new CommandContext
 * @param cmd The command
 * @param source The source of the command
 * @param args The arguments supplied by `source`
 */
class CommandContext(

        /**
         * The command that was run.
         */
        val cmd: CommandSpec,

        /**
         * Who/what sent the command. This object can also possibly answer questions such as
         * when, where, and why the command sent.
         */
        val source: CommandSource,

        args: List<CommandElement>
) {

    /**
     * The extra arguments that have no associated parameter.
     */
    val extra = args.filter { it.key == null }.map { it.input }

    /**
     * Whether any extra arguments where supplied to the command.
     */
    val hasExtra = !extra.isEmpty()

    /**
     * The arguments whose input has an associated parameter
     */
    val args = args.filter { it.key != null }

    /**
     * Returns `true` if the command wasn't given any input.
     */
    fun isEmpty() = args.isEmpty()

    /**
     * Indicates whether the command was given input
     *
     * @return `true` if the command was given input
     */
    fun isNotEmpty() = args.isNotEmpty()

    /**
     * Indicates whether the specified parameter has any input.
     *
     * @param name The name of the parameter
     * @return `true` if the parameter was found and has input
     */
    fun hasInput(name: String): Boolean = getElement(name)?.input?.isBlank() == false

    @JvmName("isPresent")
    operator fun contains(name: String) : Boolean = getElement(name) != null

    /**
     * Get the command element associated with the give name
     *
     * @param name The name of the parameter
     */
    fun getElement(name: String) = args.firstOrNull { it.key?.name == name }

    /**
     * Get the value of the specified parameter
     *
     * @param name The name of the parameter
     * @return the values
     */
    operator fun get(name: String)= getElement(name)?.value

    /**
     * Get the value of the specified parameter cast to the supplied type
     *
     * @param T The type
     * @param name The name of the parameter
     */
    @Suppress("unchecked_cast")
    fun <T> getAs(name: String) = get(name) as T

}