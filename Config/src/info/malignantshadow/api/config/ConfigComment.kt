package info.malignantshadow.api.config

/**
 * @author Shad0w (Caleb Downs)
 */
data class ConfigComment(val character: Char, var comment: String) {

    override fun toString() = "$character $comment"

}