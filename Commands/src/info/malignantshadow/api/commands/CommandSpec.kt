package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.CommandContext
import info.malignantshadow.api.commands.dispatch.CommandResult
import info.malignantshadow.api.commands.dispatch.CommandSource

/**
 * Represents a command.
 */
class CommandSpec(

        /**
         * The name.
         */
        val name: String,

        /**
         * The aliases.
         */
        val aliases: List<String>,

        /**
         * The description of this command's actions.
         */
        val desc: String,

        /**
         * The parameters.
         */
        val params: List<CommandParameter>,

        /**
         * The 'extra' information that should be displayed in help listing.
         */
        val extra: CommandParameter?,

        /**
         * The handler of this command
         */
        val handler: ((CommandContext) -> CommandResult?)?,

        /**
         * The child commands
         */
        val children: List<CommandSpec>,

        /**
         * A function to determine whether this command can be run via the given source.
         */
        val isSendableBy: (CommandSource) -> Boolean,

        /**
         * A function to determine whether this command should be hidden from help listings
         * shown to the given command source
         */
        val isHiddenFor: (CommandSource) -> Boolean
) {

    /**
     * All aliases of this command, including its name.
     */
    val allAliases = listOf(name, *aliases.toTypedArray())

    /**
     * The parameters of this command that are to be considered as flag
     */
    val flags = params.filter { it.isFlag }

    /**
     * Indicates whether this command has any children
     */
    val isParent = !children.isEmpty()

    /**
     * The minimum amount of arguments that a source must supply this command
     */
    val minArgs = params.filter { !it.isFlag }.count { it.isRequired }

    /**
     * The minimum amount of flags that a source must supply this command
     */
    val minFlags = params.filter { it.isFlag }.count { it.isRequired && !it.types.isEmpty() }

    init {
        allAliases.forEach {
            check(!it.startsWith("-")) { "A command cannot have an alias that starts with '-' (given: '$it')" }
            check(Regex("\\s") !in it) { "A command cannot have whitespace in any of its aliases" }
        }
        check(handler != null || !children.isEmpty()) {
            "Command '$name' is empty - a command must have a handler or at least one child command"
        }
    }

    /**
     * Indicates whether this command has the given alias (case-insensitive)
     *
     * @param alias The alias
     */
    fun hasAlias(alias: String) = allAliases.firstOrNull { it.equals(alias, true) } != null

    /**
     * Get the commands that should be visible in the help listing shown to `source`.
     *
     * @param source The source of a command
     * @return a List of visible commands
     */
    fun getVisibleChildren(source: CommandSource) = children.filter { !it.isHiddenFor(source) }

    /**
     * Get the commands that `source` can send.
     *
     * @param source The source of a command
     * @return a List of sendable commands
     */
    fun getSendableChildren(source: CommandSource) = children.filter { it.handler != null && it.isSendableBy(source) }

}