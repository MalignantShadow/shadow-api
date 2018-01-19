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
     *
     */
    @JvmName("isPresent")
    operator fun contains(name: String): Boolean = getElement(name)?.input?.isBlank() == false

    fun getElement(name: String) = args.firstOrNull { it.key?.name == name }

    operator fun get(name: String)= getElement(name)?.value

}