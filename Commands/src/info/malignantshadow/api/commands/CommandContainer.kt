package info.malignantshadow.api.commands

import info.malignantshadow.api.commands.dispatch.CommandSource

/**
 * Represents a command manager or a command
 */
open class CommandContainer(

        /**
         * The child commands of this container
         */
        val children: List<CommandSpec>
) {

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
    fun getSendableChildren(source: CommandSource) = children.filter { it.isSendableBy(source) }

}