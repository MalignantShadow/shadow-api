package info.malignantshadow.api.util.arguments

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.aliases.Nameable
import info.malignantshadow.api.util.extensions.equalsAny
import info.malignantshadow.api.util.random.Pattern
import info.malignantshadow.api.util.selectors.Selector

typealias Type<T> = (String?) -> T

object ArgumentTypes {

    @JvmField
    val STRING = { input: String? -> input }

    @JvmField
    val INT = { input: String? -> input?.toIntOrNull() }

    @JvmField
    val HEXADECIMAL = { input: String? -> input?.toIntOrNull(16) }

    //time?

    @JvmField
    val DOUBLE = { input: String? -> input?.toDoubleOrNull() }

    @JvmField
    val NUMBER: Type<Number?> = { input: String? -> INT(input) ?: DOUBLE(input) }

    @JvmField
    val BOOLEAN = { input: String? ->
        when {
            input == null -> null
            input.equalsAny(true, "yes", "true", "on") -> true
            input.equalsAny(true, "no", "false", "off") -> false
            else -> null
        }
    }

    @JvmField
    val PRIMITIVE: Type<Any?> = { input: String? -> BOOLEAN(input) ?: NUMBER(input) ?: input }

    @JvmField
    val SELECTOR = { input: String? -> Selector.compile(input) }

    @JvmStatic
    fun <R> pattern(transform: Type<R>) = { input: String? -> Pattern(input, transform) }

    //TODO: Allow the input to escape commas
    @JvmStatic
    fun arrayOf(vararg types: Type<Any?>): (String?) -> Array<Any?> = { input: String? ->
        if (input == null)
            emptyArray()
        else {
            val split = input.split(",")
            val values = arrayOfNulls<Any?>(split.size)
            for (i in 0 until split.size) {
                val str = split[i]
                var v: Any? = null
                for (t in types) {
                    v = t(str)
                    if (v != null) break
                }
                values[i] = v
            }
            values
        }
    }

    fun <E: Enum<E>> enumValue(values: Iterable<E>, caseSensitive: Boolean = false): Type<E?> =
            firstMatch(values) { input: String?, it: E ->
                if(it is Aliasable) {
                    //use the name property of Nameable instead of Enum
                    if ((it as Nameable).name.equals(input, !caseSensitive)) return@firstMatch true
                    it.aliases.forEach { if(it.equals(input, !caseSensitive)) return@firstMatch true }
                }
                if(it.name.equals(input, !caseSensitive)) return@firstMatch true
                return@firstMatch false
            }

    @JvmStatic
    fun <T> firstMatch(values: Iterable<T>, predicate: (String?, T) -> Boolean): Type<T?> = label@{ input: String? ->
        values.forEach { if(predicate(input, it)) return@label it }
        null
    }

    @JvmStatic
    fun bitwiseFlag(flagType: Type<Int?>): (String?) -> Int = label@ { input: String? ->
        var bits = 0
        val values = arrayOf(flagType)(input)
        values.forEach { if (it != null) bits = bits or (it as Int) }
        return@label bits
    }

}