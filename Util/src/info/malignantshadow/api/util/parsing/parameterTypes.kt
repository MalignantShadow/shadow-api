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

    val STRING = object : ParameterType<String> {

        override val pattern = ".*"

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

    fun matchesPattern(pattern: String) = object : ParameterType<String> {

        override val pattern = pattern

        override fun parse(input: String) =
                if (input.matches(Regex(pattern))) input else null

        override fun complete(partial: String) = emptyList<String>()

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

    fun choices(first: String, second: String, vararg others: String) = choices(true, first, second, *others)

    fun choices(ignoreCase: Boolean, first: String, second: String, vararg others: String) = object : ParameterType<String> {

        private val choices = listOf(first, second, *others)

        override val pattern =
                choices.joinToString(prefix = "${if (ignoreCase) "(?i)" else ""}(", postfix = ")", separator = "|") { Pattern.quote(it) }

        override fun parse(input: String) =
                if (choices.any { it.equals(input, ignoreCase) }) input
                else null

        override fun complete(partial: String): List<String> =
                choices.filter { it.startsWith(partial, ignoreCase) }

    }

    fun choices(first: Int, second: Int, vararg others: Int) = choices(INT, first, second, *others.toTypedArray())

    fun <T> choices(type: ParameterType<T>, first: T, second: T, vararg others: T) = choices(type, listOf(first, second, *others))

    fun <T> choices(type: ParameterType<T>, choices: Iterable<T>) = object : ParameterType<T> {

        override val pattern: String = type.pattern

        override fun complete(partial: String) = type.complete(partial)

        override fun parse(input: String): T? {
            val v = type.parse(input) ?: return null
            return if (v in choices) return v else null
        }

    }

    fun range(min: Int, max: Int) = range(min..max)

    fun range(range: IntRange) = object : ParameterType<Int> by INT {

        override fun parse(input: String): Int? {
            val v = INT.parse(input) ?: return null
            return if (v in range) v else null
        }

    }

    fun <T> listOf(type: ParameterType<T>, vararg others: ParameterType<T>) =
            ParameterTypes.listOf(",", type, *others)


    fun <T> listOf(delimiter: String, type: ParameterType<T>, vararg others: ParameterType<T>) =
            object : ParameterType<List<T>> {

                val types = kotlin.collections.listOf(type, *others)

                val typePattern = regexChoice(types.map { it.pattern })

                override val pattern = "$typePattern($delimiter$typePattern)+"

                private fun tokenize(input: String): List<Tokenizer.Token> {
                    val tokens = ArrayList<Tokenizer.Token>()
                    val tokenizer = Tokenizer(input, false)
                    types.forEachIndexed { index, it -> tokenizer.addTokenType(it.pattern, index) }
                    tokenizer.addTokenType(delimiter, types.size)
                    var token = tokenizer.next()
                    while(token != null) {
                        tokens.add(token)
                        token = tokenizer.next()
                    }
                    return tokens
                }

                override fun parse(input: String): List<T>? {
                    if (!matches(input)) return null
                    return tokenize(input).filter { it.type != types.size }.map { types[it.type].parse(it.match)!! }
                }

                private fun completeItem(item: String) = types.flatMap { it.complete(item) }

                override fun complete(partial: String): List<String> {
                    if(partial.isBlank() || Regex(delimiter) !in partial) return completeItem(partial)

                    val list = tokenize(partial)
                    val length = list.sumBy { it.match.length }
                    val beginning = partial.substring(0..length)
                    val sub = partial.substring(beginning.length)
                    return if(list.last().type == types.size)
                        completeItem(sub).map { beginning + it }
                    else
                        completeItem(list.last().match).map { beginning + it }
                }

            }

    fun <E : Enum<E>> enumValue(values: Iterable<E>, ignoreCase: Boolean = true) = object : ParameterType<E> {

        override val pattern =
                values.joinToString(prefix = "${if (ignoreCase) "(?i)" else ""}(", postfix = ")", separator = "|") { it.name }

        override fun parse(input: String): E? {
            values.forEach {
                if (it is Aliasable) {
                    //use the name property of Nameable instead of Enum
                    if ((it as Nameable).name.equals(input, ignoreCase)) return@parse it
                    it.hasAlias(input, ignoreCase)
                }
                if (it.name.equals(input, ignoreCase)) return@parse it
            }
            return null
        }

        override fun complete(partial: String) =
                values.map { it.name }.filter { it.startsWith(partial, ignoreCase) }

    }

    inline fun <reified E : Enum<E>> enumValue(ignoreCase: Boolean = true) =
            enumValue(kotlin.enumValues<E>().toList(), ignoreCase)

}
