package info.malignantshadow.api.config

import info.malignantshadow.api.util.arguments.ArgumentTypes

infix fun String.withValue(value: Any?): ConfigPair = ConfigPair(this, value)

class ConfigPair(val key: String, value: Any? = null) : ConfigChild(), ConfigCopyable {

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

    operator fun component1() = key
    operator fun component2() = value

    @Suppress("ReplaceGetOrSet")
    operator fun get(key: String, vararg path: String): ConfigPair? {
        return if (!isSection()) null
        else asSection().get(key, *path)
    }

    inline fun <reified T> asOrNull() = if(value is T) value as T else null

    fun isSection() = value is ConfigSection
    fun asSection() = value as ConfigSection
    fun asSectionOrNull() = asOrNull<ConfigSection>()

    fun isSequence() = value is ConfigSequence
    fun asSequence() = value as ConfigSequence
    fun asSequenceOrNull() = asOrNull<ConfigSequence>()

    fun isBoolean() = value == null || value is Boolean || value is ConfigBoolean
    fun asBoolean() = when (value) {
        null -> false
        is ConfigBoolean -> (value as ConfigBoolean).value
        else -> value as Boolean
    }
    fun asBooleanOrNull() = if(isBoolean()) asBoolean() else null

    fun isNumber() = value is Number
    fun asNumber() = when(value) {
        null -> 0
        is ConfigNumber<*> -> (value as ConfigNumber<*>).value
        is String -> ArgumentTypes.NUMBER(value as String)
        else -> value as Number
    }
    fun asNumberOrNull() = if(isNumber()) asNumber() else null

    fun isString() = value is String
    fun asString() = when {
        isString() -> value as String
        value == null -> "null"
        else -> value.toString()
    }

    override fun equals(other: Any?) = if(other is ConfigPair) equals(other) else super.equals(other)

    fun equals(other: ConfigPair): Boolean {
        if(other === this) return true
        return key == other.key && value == other.value
    }

    override fun copy() = when(value) {
        is ConfigCopyable -> ConfigPair(key, (value as ConfigCopyable).copy())
        else -> ConfigPair(key, value)
    }

    override fun isLastInTree(): Boolean {
        val section = parent as? ConfigSection ?: throw IllegalStateException("parent is not ConfigSection")
        val lastLocal = section.pairs[section.lastIndex].key == key
        return lastLocal && section.isLastInTree()
    }

}