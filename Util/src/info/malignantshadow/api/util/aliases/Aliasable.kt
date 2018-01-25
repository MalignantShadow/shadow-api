package info.malignantshadow.api.util.aliases

/**
 * Represents an object that can have aliases
 * @author Shad0w (Caleb Downs)
 */
interface Aliasable : Nameable {

    /**
     * The aliases of this object
     */
    val aliases: List<String>

    operator fun contains(alias: String)= hasAlias(alias)

    fun hasAlias(alias: String, ignoreCase: Boolean = false) =
            alias.equals(name, ignoreCase) || aliases.any { it.equals(alias, ignoreCase) }

}