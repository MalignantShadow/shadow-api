package info.malignantshadow.api.config

/**
 * @author Shad0w (Caleb Downs)
 */
open class ConfigValue<out T>(val value: T, val literal: String): ConfigCopyable {

    fun component1() = value
    fun component2() = literal

    @Suppress("UNCHECKED_CAST")
    override fun copy() = when(value) {
        is ConfigCopyable -> ConfigValue(value.copy() as T, literal)
        else -> ConfigValue(value, literal)
    }

    override fun equals(other: Any?): Boolean = if(other is ConfigValue<*>) equals(other) else equals(other)

    fun equals(other: ConfigValue<*>)= other === this || other.value == value

}