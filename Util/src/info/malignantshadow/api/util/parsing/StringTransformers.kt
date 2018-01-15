package info.malignantshadow.api.util.parsing

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.aliases.Nameable
import info.malignantshadow.api.util.equalsAny
import info.malignantshadow.api.util.random.Pattern
import info.malignantshadow.api.util.selectors.Selector

typealias Type<T> = (String?) -> T

/**
 * A utility object holding different types of argument types
 * @author Shad0w (Caleb Downs)
 */
object StringTransformers {

    /**
     * Parse the input as a String. Simply returns the input.
     */
    @JvmField
    val STRING = { input: String? -> input }

    /**
     * Parse the input as an Int, or null if it can't be parsed as such.
     */
    @JvmField
    val INT = { input: String? -> input?.toIntOrNull() }

    /**
     * Parse the input as a hexadecimal Int, or null if it can't be parsed as such.
     */
    @JvmField
    val HEXADECIMAL = { input: String? -> input?.toIntOrNull(16) }


    /**
     * Parse the input as an Double, or null if it can't be parsed as such.
     */
    @JvmField
    val DOUBLE = { input: String? -> input?.toDoubleOrNull() }

    /**
     * Parse the input as an Number, or null if it can't be parsed as such.
     * [INT] will be tried first, otherwise [DOUBLE] will be tried.
     */
    @JvmField
    val NUMBER: Type<Number?> = { input: String? -> INT(input) ?: DOUBLE(input) }

    /**
     * Parse the input as a Boolean, or null if it can't be parsed as such.
     *
     * * Aside from `true`, `yes` and `on` are accepted inputs that evaluate to `true`
     * * Aside from `false`, `no` and `off` are accepted inputs that evaluate to `false`
     */
    @JvmField
    val BOOLEAN = { input: String? ->
        when {
            input == null -> null
            input.equalsAny(true, "yes", "true", "on") -> true
            input.equalsAny(true, "no", "false", "off") -> false
            else -> null
        }
    }

    /**
     * Parse the input as a primitive value. [Boolean] will be tried first, then [NUMBER], if either
     * functions return `null`, then the input is simply returned.
     */
    @JvmField
    val PRIMITIVE: Type<Any?> = { input: String? -> BOOLEAN(input) ?: NUMBER(input) ?: input }

    /**
     * Parse the input as a [Selector], or null if it can't be parsed as such.
     */
    @JvmField
    val SELECTOR = { input: String? -> Selector.compile(input) }


    /**
     * Parse the input as an [Pattern], or null if it can't be parsed as such.
     *
     * @param transform The transform function to pass to the constructed Pattern
     */
    @JvmStatic
    fun <R> pattern(transform: Type<R>) = { input: String? -> Pattern(input, transform) }

    /**
     * Parse the input as an array of any of the given types
     *
     * @param types The accepted types
     */
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

    /**
     * Parse the input as an Enum value
     *
     * @param values The available values
     * @param caseSensitive Whether the names/aliases of the Enum values are matched case-sensitive the input string
     * @return a function that returns an Enum value, or null if no match was found
     */
    fun <E: Enum<E>> enumValue(values: Iterable<E>, caseSensitive: Boolean = false): Type<E?> =
            firstMatch(values) { input: String?, it: E ->
                if (it is Aliasable) {
                    //use the name property of Nameable instead of Enum
                    if ((it as Nameable).name.equals(input, !caseSensitive)) return@firstMatch true
                    it.aliases.forEach { if (it.equals(input, !caseSensitive)) return@firstMatch true }
                }
                if (it.name.equals(input, !caseSensitive)) return@firstMatch true
                return@firstMatch false
            }

    /**
     * Parse the input by returning the first match of the specified values, given the specified predicate
     *
     * @param values The available values
     * @param predicate The predicate
     * @return a function that returns the first match
     */
    @JvmStatic
    fun <T> firstMatch(values: Iterable<T>, predicate: (String?, T) -> Boolean): Type<T?> = label@{ input: String? ->
        values.forEach { if(predicate(input, it)) return@label it }
        null
    }

    /**
     * Parse the input as an Int representing bitwise flags. The input is parsed as an [arrayOf] `Int`s,
     * using the specified function. The Ints are then bitwise OR'd together.
     *
     * @param flagType A function that parses the input as a flag
     * @return a function that returns an Int representing bitwise flags
     */
    @JvmStatic
    fun bitwiseFlag(flagType: Type<Int?>): (String?) -> Int = label@ { input: String? ->
        var bits = 0
        val values = arrayOf(flagType)(input)
        values.forEach { if (it != null) bits = bits or (it as Int) }
        return@label bits
    }

}