package info.malignantshadow.api.config

/**
 * Represents a [ConfigSection], [ConfigPair], or [ConfigSequence]
 * @author Shad0w (Caleb Downs)
 */
abstract class ConfigChild {

    open internal var parentInternal: ConfigChild? = null

    /**
     * The parent of this object
     */
    val parent: ConfigChild? get() = parentInternal
}
