package info.malignantshadow.api.util.parsing

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.aliases.Nameable
import info.malignantshadow.api.util.equalsAny
import info.malignantshadow.api.util.random.Pattern
import info.malignantshadow.api.util.selectors.Selector

typealias ParameterToken<T> = (String) -> T

/**
 * A utility object holding different types of argument types
 * @author Shad0w (Caleb Downs)
 */
object ParameterType {

    /**
     * Parse the input as a String. Simply returns the input.
     */
    @JvmField
    val STRING = { input: String -> input }

    /**
     * Parse the input as an Int, or null if it can't be parsed as such.
     */
    @JvmField
    val INT = { input: String -> input?.toIntOrNull() }

    /**
     * Parse the input as a hexadecimal Int, or null if it can't be parsed as such.
     */
    @JvmField
    val HEXADECIMAL = { input: String -> input?.toIntOrNull(16) }


    /**
     * Parse the input as an Double, or null if it can't be parsed as such.
     */
    @JvmField
    val DOUBLE = { input: String -> input?.toDoubleOrNull() }

    /**
     * Parse the input as an Number, or null if it can't be parsed as such.
     * [INT] will be tried first, otherwise [DOUBLE] will be tried.
     */
    @JvmField
    val NUMBER: ParameterToken<Number?> = { input: String -> INT(input) ?: DOUBLE(input) }

    /**
     * Parse the input as a Boolean, or null if it can't be parsed as such.
     *
     * * Aside from `true`, `yes` and `on` are accepted inputs that evaluate to `true`
     * * Aside from `false`, `no` and `off` are accepted inputs that evaluate to `false`
     */
    @JvmField
    val BOOLEAN = { input: String ->
        when {
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
    val PRIMITIVE: ParameterToken<Any?> = { input: String -> BOOLEAN(input) ?: NUMBER(input) ?: input }

    /**
     * Parse the input as a [Selector], or null if it can't be parsed as such.
     */
    @JvmField
    val SELECTOR = { input: String -> Selector.compile(input) }


    /**
     * Parse the input as an [Pattern], or null if it can't be parsed as such.
     *
     * @param transform The transform function to pass to the constructed Pattern
     */
    @JvmStatic
    fun <R> pattern(transform: (String?) -> R) = { input: String -> Pattern(input, transform) }

    /**
     * Parse the input as a list of any of the given types
     *
     * @param types The accepted types
     */
    @JvmStatic
    fun listOf(vararg types: ParameterToken<Any?>): (String) -> List<Any?> = { input: String? ->
        if (input == null)
            emptyList()
        else {
            val split = input.split("(?<!\\\\),")
            val values = ArrayList<Any?>()
            for (i in 0 until split.size) {
                val str = split[i]
                var v: Any? = null
                for (t in types) {
                    v = t(str)
                    if (v != null) break
                }
                values[i] = v
            }
            values.toList()
        }
    }

    /**
     * Parse the input as an Enum value
     *
     * @param E The Enum
     * @param caseSensitive Whether the names/aliases of the Enum values are matched case-sensitive the input string
     * @return a function that returns an Enum value, or null if no match was found
     */
    inline fun <reified E: Enum<E>> enumValue(caseSensitive: Boolean = false) =
            enumValue(enumValues<E>().toList(), caseSensitive)

    /**
     * Parse the input as an Enum value
     *
     * @param values The available values
     * @param caseSensitive Whether the names/aliases of the Enum values are matched case-sensitive the input string
     * @return a function that returns an Enum value, or null if no match was found
     */
    fun <E: Enum<E>> enumValue(values: Iterable<E>, caseSensitive: Boolean = false): ParameterToken<E?> =
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
    fun <T> firstMatch(values: Iterable<T>, predicate: (String, T) -> Boolean): ParameterToken<T?> = label@{ input: String ->
        values.forEach { if(predicate(input, it)) return@label it }
        null
    }

    /**
     * Specify that the input must be one of the given choices (case-sensitive)
     *
     * @param choices The available choices
     * @return a function that will return the choice if it matches, or `null` if there was no match
     */
    @JvmStatic
    fun oneOf(vararg choices: String): ParameterToken<String?> {
        return oneOf(false, *choices)
    }

    /**
     * Specify that the input must be one of the given choices
     *
     * @param choices The available choices
     * @param ignoreCase Whether to ignore casing
     * @return a function that will return the choice if it matches, or `null` if there was no match
     */
    @JvmStatic
    fun oneOf(ignoreCase: Boolean, vararg choices: String): ParameterToken<String?> {
        require(!choices.isEmpty()) { "Must specify at least one choice" }
        return firstMatch(listOf(*choices)) { input, it -> it.equals(input, ignoreCase) }
    }

    /**
     * Parse the input as an Int representing bitwise flags. The input is parsed as an [listOf] `Int`s,
     * using the specified function. The Ints are then bitwise OR'd together.
     *
     * @param flagType A function that parses the input as a flag
     * @return a function that returns an Int representing bitwise flags
     */
    @JvmStatic
    fun bitwiseFlag(flagType: ParameterToken<Int?>): (String) -> Int = label@ { input: String ->
        var bits = 0
        val values = listOf(flagType)(input)
        values.forEach { if (it != null) bits = bits or (it as Int) }
        return@label bits
    }

}