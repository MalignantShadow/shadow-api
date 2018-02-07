package info.malignantshadow.api.commands

open class CommandContainer(

        /**
         * The child commands
         */
        val children: List<Command>
) {

    /**
     * Get the first command that has the given alias
     */
    @JvmName("getCommand")
    operator fun get(name: String) = children.firstOrNull { it.hasAlias(name, true) }

}