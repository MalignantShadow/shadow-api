package info.malignantshadow.api.config

/**
 * Represents a [ConfigSection], [ConfigPair], or [ConfigSequence]
 * @author Shad0w (Caleb Downs)
 */
abstract class ConfigChild {

    open internal var parentInternal: ConfigChild? = null
    val parent: ConfigChild? get() = parentInternal
}
