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

}