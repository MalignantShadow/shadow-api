package info.malignantshadow.api.config

import info.malignantshadow.api.util.parsing.ArgumentTypes

/**
 *
 */
infix fun String.withValue(value: Any?): ConfigPair = ConfigPair(this, value)

/**
 *
 * @author Shad0w (Caleb Downs)
 */
class ConfigPair(val key: String, value: Any? = null) : ConfigChild(), ConfigCopyable {

    /**
     * The value
     */
    var value = value
        set(value) {
            field = value
            if (value is ConfigPair) value.parentInternal = this
        }

    override var parentInternal: ConfigChild?
        get() = super.parentInternal
        set(value) {
            require(value is ConfigSection) { "value is not a ConfigSection" }
            super.parentInternal = value
        }

    /**
     * The key
     */
    operator fun component1() = key

    /**
     * The value
     */
    operator fun component2() = value

    /**
     * Get a ConfigPair from this ConfigPair, if the value is a [ConfigSection]
     * @param key The key
     * @param path Other keys to act as a path
     * @return the value, or null if the value is not a [ConfigSection]
     */
    @Suppress("ReplaceGetOrSet")
    operator fun get(key: String, vararg path: String): ConfigPair? {
        return if (!isSection()) null
        else asSection().get(key, *path)
    }

    /**
     * Indicates whether the value of this pair is a [ConfigValue]
     * @return true if the value is a [ConfigSection]
     */
    fun isSection() = value is ConfigSection

    /**
     * Get the value of this pair as a [ConfigSection]
     * @return the value as a [ConfigSection]
     */
    fun asSection() = value as ConfigSection

    /**
     * Indicates whether the value of this pair is a [ConfigSequence]
     * @return true if this value is a [ConfigSequence]
     */
    fun isSequence() = value is ConfigSequence

    /**
     * Get the value of this pair as a [ConfigSequence]
     */
    fun asSequence() = value as ConfigSequence

    /**
     * Indicates whether the value of this pair is a [Boolean] or a [ConfigBoolean]
     */
    fun isBoolean() = value == null || value is Boolean || value is ConfigBoolean

    /**
     * Get the value of this pair as a [Boolean]
     * @return the value as a Boolean
     */
    fun asBoolean() = when (value) {
        null -> false
        is ConfigBoolean -> (value as ConfigBoolean).value
        else -> value as Boolean
    }

    /**
     * Indicates whether the value of this pair is a Number
     */
    fun isNumber() = value is Number

    /**
     * Get the value of this pair as a [Number]
     * @return the value as a Number
     */
    fun asNumber() = when (value) {
        null -> 0
        is ConfigNumber<*> -> (value as ConfigNumber<*>).value
        is String -> ArgumentTypes.NUMBER(value as String)
        else -> value as Number
    }

    /**
     * Indicates whether the value of this pair is a String
     */
    fun isString() = value is String

    /**
     * Get the value of this pair as String
     *
     * Equivalent to `value.toString()`
     */
    fun asString() = value.toString()

    override fun equals(other: Any?) = if (other is ConfigPair) equals(other) else super.equals(other)

    /**
     * Indicates whether the given ConfigPair is equal to this one. More specifically,
     * if the two keys and values are equal to each other
     */
    fun equals(other: ConfigPair) = other === this || (other.key == key && other.value == value)

    override fun copy() = when (value) {
        is ConfigCopyable -> ConfigPair(key, (value as ConfigCopyable).copy())
        else -> ConfigPair(key, value)
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

}