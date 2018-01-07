package info.malignantshadow.api.config

/**
 * Represents a configuration value that can be represented as a string
 *
 * @param value The value
 * @param literal The string that represents the value
 * @author Shad0w (Caleb Downs)
 */
open class ConfigValue<out T>(val value: T, val literal: String) : ConfigCopyable {

    /**
     * The value
     */
    fun component1() = value

    /**
     * The String literal
     */
    fun component2() = literal

    @Suppress("UNCHECKED_CAST")
    override fun copy() = when (value) {
        is ConfigCopyable -> ConfigValue(value.copy() as T, literal)
        else -> ConfigValue(value, literal)
    }

    override fun equals(other: Any?): Boolean = if (other is ConfigValue<*>) equals(other) else equals(other)

    /**
     * Indicates whether or not the given ConfigValue is equal to this one. More specifically
     * if `this === other` || other.value == this.value
     *
     * @param other The other value
     * @return `true` if the values are equal to each
     */
    fun equals(other: ConfigValue<*>) = other === this || other.value == value

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + literal.hashCode()
        return result
    }

}