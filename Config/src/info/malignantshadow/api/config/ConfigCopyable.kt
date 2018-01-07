package info.malignantshadow.api.config

/**
 * Represents a Configuration Object that can be copied.
 * @author Shad0w (Caleb Downs)
 */
interface ConfigCopyable {

    /**
     * Produce a copy of this object
     */
    fun copy(): ConfigCopyable

}