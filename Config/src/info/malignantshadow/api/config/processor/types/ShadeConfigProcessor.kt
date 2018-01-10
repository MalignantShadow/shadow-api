package info.malignantshadow.api.config.processor.types

import info.malignantshadow.api.config.ConfigBoolean
import info.malignantshadow.api.config.ConfigCopyable
import info.malignantshadow.api.config.ConfigPair
import info.malignantshadow.api.config.ConfigSection
import info.malignantshadow.api.config.ConfigSequence
import info.malignantshadow.api.config.ConfigValue
import info.malignantshadow.api.config.processor.TextFileConfigProcessor
import info.malignantshadow.api.util.escape
import info.malignantshadow.api.util.indent
import info.malignantshadow.api.util.unescape
import info.malignantshadow.api.util.parsing.Tokenizer
import java.io.FileWriter
import java.io.IOException

/**
 *
 * A [info.malignantshadow.api.config.ConfigProcessor] that is used to handle Shade configurations. Internally,
 * this class utilizes an instance of [Tokenizer] to read through the source and parse the Shade string accordingly.
 *
 * @author Shad0w (Caleb Downs)
 */
class ShadeConfigProcessor : TextFileConfigProcessor() {

    companion object {

        private const val STRING = 0
        private const val DOUBLE = 1
        private const val INT = 2
        private const val NULL = 6
        private const val TRUE = 7
        private const val FALSE = 8
        private const val START_MAP = 9
        private const val END_MAP = 10
        private const val KEY_SEPARATOR = 11
        private const val START_ARRAY = 12
        private const val END_ARRAY = 13
        private const val LINE_COMMENT = 14
        private const val BLOCK_COMMENT = 15
        private const val IDENTIFIER = 16

        /**
         * The default indentation size
         */
        const val DEF_INDENT_SIZE = 2

        /**
         * A Regular Expression that matches a token that would evaluate to `true`
         */
        val REGEX_TRUE = Tokenizer.keyword("true", "on", "yes")
        /**
         * A Regular Expression that matches a token that would evaluate to `false`
         */
        val REGEX_FALSE = Tokenizer.keyword("false", "off", "no")

        private fun createTokenizer(): Tokenizer {
            val t = Tokenizer("")
            t.addTokenType(Tokenizer.string() + "(?![^\\s\\[\\]{}])", STRING)
            t.addTokenType(Tokenizer.REGEX_DOUBLE, DOUBLE)
            t.addTokenType(Tokenizer.REGEX_INT, INT)
//          t.addTokenType("\\(", START_EXPR)
//          t.addTokenType("\\)", END_EXPR)
//          t.addTokenType("[+\\-/*%&|]", OPERATION)
            t.addTokenType(Tokenizer.keyword("null"), NULL)
            t.addTokenType(REGEX_TRUE, TRUE)
            t.addTokenType(REGEX_FALSE, FALSE)
            t.addTokenType("\\{", START_MAP)
            t.addTokenType("\\}", END_MAP)
            t.addTokenType(",", KEY_SEPARATOR)
            t.addTokenType("\\[", START_ARRAY)
            t.addTokenType("\\]", END_ARRAY)
            t.addTokenType(
                    Tokenizer.blockComment("###") + "|" +
                            Tokenizer.blockComment("/\\*", "\\*/"),
                    BLOCK_COMMENT, true)
            t.addTokenType(Tokenizer.lineComment("(#|;|//)"), LINE_COMMENT, true)
            t.addTokenType(Tokenizer.REGEX_IDENTIFIER_WITH_DASHES, IDENTIFIER)
            return t
        }

        private val staticTokenizer = createTokenizer()

        private fun stringifyKey(key: String): String {
            val token = staticTokenizer.independent(key)
            return if (token == null || token.type != IDENTIFIER) "\"${key.escape()}\""
            else key
        }

        private fun stringifyKeys(pairs: List<ConfigPair>) =
                pairs.joinToString(", ", postfix = " ") { stringifyKey(it.key) }

        /**
         * Stringify a ConfigSection. If `root` is `true` and `indentSize` is `0`, then the ending braces
         * of lists and maps will be removed from the end of the result string. More specifically,
         * `replace(Regex("(\\s*[}\\]]\\s*)*\$"), "")` is invoked on the result string.
         *
         * @param section The ConfigSection to stringify
         * @param indentSize The indenting size
         * @param indent The initial level of indentation
         * @param root Whether the given ConfigSection is the root of the configuration. If `true`, curly
         * braces will not be added in the ends of the string
         * @return a string that accurately represents the given ConfigSection
         */
        @JvmStatic
        @JvmOverloads
        fun stringify(section: ConfigSection, indentSize: Int = DEF_INDENT_SIZE, indent: Int = 0, root: Boolean = true): String {
            val indentString = "".indent(indentSize, indent)
            val grouped = section.groupBy { it.value }
            val str = stringifyIterable(
                    if (root) "" else "{",
                    if (root) "" else "}",
                    grouped.entries,
                    indentSize,
                    indent
            ) {
                indentString + stringifyKeys(it.value) + stringify(it.key, indentSize, indent + 1)
            }

            return if (root && indentSize <= 0) str.replace(Regex("(\\s*[}\\]]\\s*)*\$"), "") else str
        }

        /**
         * Stringify a ConfigSequence
         *
         * @param seq The ConfigSequence
         * @param indentSize The indenting size
         * @param indent The initial level of indentation
         * @return a string that accurately represents the given ConfigSequence
         */
        @JvmStatic
        @JvmOverloads
        fun stringify(seq: ConfigSequence, indentSize: Int = DEF_INDENT_SIZE, indent: Int = 0): String {
            val indentString = "".indent(indentSize, indent)
            return stringifyIterable(
                    "[",
                    "]",
                    seq,
                    indentSize,
                    indent
            ) {
                "$indentString${stringify(it, indentSize, indent + 1)}"
            }
        }

        private fun <T> stringifyIterable(
                start: String,
                end: String,
                iterable: Iterable<T>,
                indentSize: Int,
                indent: Int,
                transform: (T) -> String
        ): String {
            if (iterable.none()) return "$start$end"
            val indentString = "".indent(indentSize, Math.max(0, indent - 1)) // only applied to ending character
            val shouldIndent = indentSize > 0
            val separator = if (shouldIndent) "\n" else " "
            val prefix = "$start${if (shouldIndent) "\n" else ""}"
            val postfix = "${if (shouldIndent) "\n$indentString" else ""}$end"
            return iterable.joinToString(separator, prefix, postfix, transform = transform)
        }


        /**
         * Stringify a value into the appropriate Shade format
         *
         * @param value The value
         * @param indentSize The indenting size
         * @param indent The initial level of indentation
         * @return a string that accurately represents the given value
         */
        @JvmStatic
        @JvmOverloads
        fun stringify(value: Any?, indentSize: Int = 0, indent: Int = 0): String =
                when (value) {
                    is String -> "\"${value.toString().escape()}\""
                    is ConfigSection -> stringify(value, indentSize, indent, false).trim()
                    is ConfigSequence -> stringify(value, indentSize, indent).trim()
                    is ConfigValue<*> -> value.literal
                    else -> value.toString()
                }

    }

    private val tokenizer by lazy { createTokenizer() }

    /**
     * Get a [ConfigSection] from a Shade config string
     *
     * @param src The source string
     * @return a [ConfigSection] accurately represented by the given source
     */
    @Synchronized
    override fun get(src: String): ConfigSection {
        tokenizer.src = src
        return getMap(false)
    }

    private fun getMap(lookForCloser: Boolean = true): ConfigSection {
        val section = ConfigSection()
        var token: Tokenizer.Token? = tokenizer.nextOrError()

        // If the ending tag '}' is found, then bail if it isn't required
        // Otherwise, return the empty section
        // 'token' is not null at this point, '?.' is used to appease compiler
        if (token?.type == END_MAP)
            if (lookForCloser) return section
            else tokenizer.unexpected(token.match)

        fun expectKey(token: Tokenizer.Token) {
            if (token.type != IDENTIFIER && token.type != STRING)
                tokenizer.unexpected(token.match, "IDENTIFIER or STRING")
        }

        // Continue adding pairs till '}' or EOF
        while (true) {
            if (token == null || token.type == END_MAP) break

            // we expect he next token should be an identifier, to act as the key
            // if it is a keyword (NULL|TRUE|FALSE) or a number (DOUBLE|INT),
            // then a special error message is needed
            when (token.type) {
                NULL, TRUE, FALSE, DOUBLE, INT ->
                    tokenizer.error("Value name cannot be a " +
                            "${if (token.type == DOUBLE || token.type == INT) "number" else "keyword"} (${token.match}) " +
                            "unless enclosed in quotes")
            }

            expectKey(token)

            //get keys
            val keys = ArrayList<String>()
            while (true) {
                token!!

                keys.add(
                        if(token.type == STRING)
                            token.match.substring(1, token.match.lastIndex).unescape()
                        else token.match)
                token = tokenizer.nextOrError()

                if (token.type != KEY_SEPARATOR) break
                token = tokenizer.nextOrError()

                expectKey(token)
            }

            val value = getValue(token!!)
            keys.forEach {
                val pair = ConfigPair(it)
                pair.value = (value as? ConfigCopyable)?.copy() ?: value
                section.add(pair)
                pair.parentInternal
            }

            token = tokenizer.next()
        }
        return section
    }

    private fun getArray(): ConfigSequence {
        val seq = ConfigSequence()
        var token = tokenizer.next()

        while (true) {
            if (token == null || token.type == END_ARRAY) break
            seq.add(getValue(token))

            token = tokenizer.next()
        }
        return seq
    }

    private fun getValue(token: Tokenizer.Token): Any? = when (token.type) {
        STRING -> token.match.substring(1 until token.match.lastIndex).unescape()
        DOUBLE -> token.match.toDouble()
        INT -> token.match.toInt()
        FALSE -> ConfigBoolean(false, token.match)
        TRUE -> ConfigBoolean(true, token.match)
        NULL -> null
        START_MAP -> getMap()
        START_ARRAY -> getArray()
        else -> tokenizer.unexpected(token.match, "a STRING, DOUBLE, INT, TRUE, FALSE, NULL, START_MAP, START_ARRAY")
    }

    override fun set(writer: FileWriter, section: ConfigSection): Boolean {
        return putDocument(section, writer, DEF_INDENT_SIZE)
    }

    @JvmOverloads
    fun putDocument(section: ConfigSection, writer: FileWriter, indentSize: Int, indent: Int = 0): Boolean =
            try {
                writer.write(stringify(section, indentSize, indent))
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }

}