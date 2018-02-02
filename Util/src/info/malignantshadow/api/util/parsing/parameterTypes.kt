package info.malignantshadow.api.util.parsing

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.aliases.Nameable
import java.util.regex.Pattern

interface ParameterType<out T> {

    val pattern: String

    fun parse(input: String): T?

    fun complete(partial: String): List<String>

    fun matches(input: String) = input.matches(Regex(pattern))

}

object ParameterTypes {

    val NON_WHITESPACE = object : ParameterType<String> {

        override val pattern = "\\S+"

        override fun parse(input: String): String = input

        override fun complete(partial: String): List<String> = emptyList()

    }

    val UNSIGNED_INT = object : ParameterType<Int> {

        override val pattern = "\\d+\\b"

        override fun parse(input: String): Int? {
            if (!matches(input)) return null
            return input.toInt()
        }

        override fun complete(partial: String) = emptyList<String>()

    }

    val INT = object : ParameterType<Int> by UNSIGNED_INT {

        override val pattern = "[-+]?${UNSIGNED_INT.pattern}"

    }

    val UNSIGNED_DOUBLE = object : ParameterType<Double> {

        override val pattern = "\\d*(\\.\\d+([eE][+-]?\\d+)?)\\b"

        override fun parse(input: String): Double? {
            if (!matches(input)) return null
            return input.toDouble()
        }

        override fun complete(partial: String): List<String> {
            if (partial.endsWith(".")) return listOf("${partial}0")
            return emptyList()
        }

    }

    val DOUBLE = object : ParameterType<Double> by UNSIGNED_DOUBLE {

        override val pattern: String = "[-+]?${UNSIGNED_DOUBLE.pattern}"

    }

    val NUMBER = first(INT, DOUBLE)

    val UNSIGNED_NUMBER = first(UNSIGNED_INT, UNSIGNED_DOUBLE)

    val HEX_INT = object : ParameterType<Int> {

        override val pattern: String = "#[a-fA-F0-9]+"

        override fun parse(input: String): Int? {
            if (!matches(input)) return null
            return input.toInt(16)
        }

        override fun complete(partial: String): List<String> {
            if (partial == "#") return listOf("#0")
            return emptyList()
        }

    }

    val INT_RANGE = object : ParameterType<IntRange> {

        override val pattern = "${INT.pattern}\\.\\.${INT.pattern}"

        override fun parse(input: String): IntRange? {
            if (!matches(input)) return null
            val split = input.split("..")
            return IntRange(split[0].toInt(), split[1].toInt())
        }

        override fun complete(partial: String): List<String> {
            if (partial.matches(Regex("${INT.pattern}\\.\\."))) {
                val i = partial.substring(0..partial.length - 2).toInt()
                return listOf("$i..${i + 1}")
            }
            return emptyList()
        }

    }

    val BOOLEAN = first(literal(true, "true"), literal(false, "false"))

    private fun regexChoice(patterns: List<String>) =
            patterns.joinToString(prefix = "(", postfix = ")", separator = "|") { it }

    fun <T> literal(value: T, keyword: String, ignoreCase: Boolean = true, vararg others: String) = object : ParameterType<T> {

        val keywords = listOf(keyword, *others)

        override val pattern =
                keywords.joinToString(prefix = "(", postfix = ")", separator = "|") { Pattern.quote(it) }

        override fun parse(input: String): T? {
            if (!matches(input)) return null
            return value
        }

        override fun complete(partial: String) =
                keywords.filter { it.startsWith(partial, ignoreCase) }

    }

    fun <T> first(vararg types: ParameterType<T>) = first(listOf(*types))

    fun <T> first(types: Iterable<ParameterType<T>>) = object : ParameterType<T> {

        override val pattern: String = regexChoice(types.map { it.pattern })

        override fun parse(input: String): T? {

            types.forEach {
                val v = it.parse(input)
                if (v != null) return@parse v
            }
            return null
        }

        override fun complete(partial: String): List<String> {
            val list = ArrayList<String>()
            types.forEach { list.addAll(it.complete(partial)) }
            return list
        }

    }

    fun choices(ignoreCase: Boolean = true, vararg choices: String): ParameterType<String> {
        require(choices.isNotEmpty()) { "'choices' must not be empty" }
        return object : ParameterType<String> {

            override val pattern =
                    choices.joinToString(prefix = "${if (ignoreCase) "(?i)" else ""}(", postfix = ")", separator = "|") { Pattern.quote(it) }

            override fun parse(input: String) =
                    if (choices.any { it.equals(input, ignoreCase) }) input
                    else null

            override fun complete(partial: String): List<String> =
                    choices.filter { it.startsWith(partial, ignoreCase) }

        }
    }

//    fun <T> listOf(type: ParameterType<T>, allowSpaces: Boolean = false, vararg others: ParameterType<T>) = object : ParameterType<List<T>> {
//
//        val types = kotlin.collections.listOf(type, *others)
//
//        val typePattern = regexChoice(types.map { it.pattern })
//        private val spaces = if (allowSpaces) "\\s*" else ""
//
//        override val pattern = "$typePattern($spaces,$typePattern)+"
//
//        override fun parse(input: String): List<T>? {
//            if (!matches(input)) return emptyList()
//
//        }
//
//        override fun complete(partial: String): List<String> {
//            // complete last token
//        }
//
//    }

    fun <E : Enum<E>> enumValue(values: Iterable<E>, ignoreCase: Boolean = true) = object : ParameterType<E> {

        override val pattern =
                values.joinToString(prefix = "${if (ignoreCase) "(?i)" else ""}(", postfix = ")", separator = "|") { it.name }

        override fun parse(input: String): E? {
            values.forEach {
                if (it is Aliasable) {
                    //use the name property of Nameable instead of Enum
                    if ((it as Nameable).name.equals(input, ignoreCase)) return@parse it
                    it.aliases.forEach { alias -> if (alias.equals(input, ignoreCase)) return@parse it }
                }
                if (it.name.equals(input, ignoreCase)) return@parse it
                return@parse it
            }
            return null
        }

        override fun complete(partial: String) =
                values.map { it.name }.filter { it.startsWith(partial, ignoreCase) }

    }

    inline fun <reified E : Enum<E>> enumValue(ignoreCase: Boolean = true) =
            enumValue(kotlin.enumValues<E>().toList())

}