package info.malignantshadow.api.config.processor.types

import info.malignantshadow.api.config.ConfigBoolean
import info.malignantshadow.api.config.ConfigSection
import info.malignantshadow.api.config.ConfigSequence
import info.malignantshadow.api.config.processor.TextFileConfigProcessor
import info.malignantshadow.api.util.parsing.ArgumentTypes
import info.malignantshadow.api.util.escape
import info.malignantshadow.api.util.indent
import info.malignantshadow.api.util.unescape
import info.malignantshadow.api.util.parsing.Tokenizer
import java.io.FileWriter
import java.io.IOException

/**
 * A [info.malignantshadow.api.config.ConfigProcessor] that is used to handle JSON configurations. Internally,
 * this class utilizes an instance of [Tokenizer] to read through the source and parse the JSON accordingly.
 *
 * @author Shad0w (Caleb Downs)
 */
class JsonConfigProcessor : TextFileConfigProcessor() {

    companion object {

        private const val STRING = 0
        private const val COLON = 1
        private const val COMMA = 2
        private const val START_OBJECT = 3
        private const val END_OBJECT = 4
        private const val START_ARRAY = 5
        private const val END_ARRAY = 6
        private const val TRUE = 7
        private const val FALSE = 8
        private const val NULL = 9
        private const val DOUBLE = 10
        private const val INT = 11

        /**
         * The default indent size when producing JSON output
         */
        const val DEF_INDENT_SIZE = 2

        /**
         * Stringify a [ConfigSection] as a JSON Object
         *
         * @param section The ConfigSection
         * @param indentSize The indenting size
         * @param indent The initial level of indentation
         * @return a string that accurately represents the given ConfigSection
         */
        @JvmStatic
        @JvmOverloads
        fun stringify(section: ConfigSection, indentSize: Int = DEF_INDENT_SIZE, indent: Int = 0): String {
            val indentString = "".indent(indentSize, indent + 1)
            return stringify('{', '}', section, indentSize, indent) {
                "$indentString\"${it.key.escape()}\":${if (indentSize > 0) " " else ""}${stringify(it.value, indentSize, indent + 1)}"
            }
        }

        /**
         * Stringify a [ConfigSequence] as a JSON Array
         *
         * @param seq The ConfigSequence
         * @param indentSize The indenting size
         * @param indent The initial level of indentation
         * @return a string that accurately represents the given ConfigSequence
         */
        @JvmStatic
        @JvmOverloads
        fun stringify(seq: ConfigSequence, indentSize: Int = DEF_INDENT_SIZE, indent: Int = 0): String {
            val indentString = "".indent(indentSize, indent + 1)
            return stringify('[', ']', seq, indentSize, indent) {
                "$indentString${stringify(it, indentSize, indent + 1)}"
            }
        }

        private fun <T> stringify(
                start: Char,
                end: Char,
                iterable: Iterable<T>,
                indentSize: Int,
                indent: Int,
                transform: (T) -> String
        ): String {
            if (iterable.none()) return "$start$end"
            val indentString = "".indent(indentSize, indent)
            val shouldIndent = indentSize > 0
            val separator = ",${if (shouldIndent) "\n" else ""}"
            val prefix = "$start${if (shouldIndent) "\n" else ""}"
            val postfix = "${if (shouldIndent) "\n$indentString" else ""}$end"
            return iterable.joinToString(separator, prefix, postfix, transform = transform)
        }

        /**
         * Stringify a value into the appropriate JSON format
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
                    null -> "null"
                    is String -> value.escape()
                    is ConfigSection -> stringify(value, indentSize, indent).trim()
                    is ConfigSequence -> stringify(value, indentSize, indent).trim()
                    is ConfigBoolean -> value.value.toString()
                    else -> value.toString()
                }

    }

    private val tokenizer: Tokenizer by lazy {
        val t = Tokenizer("")
        // '2' used because the given regex is put in it's own group
        t.addTokenType(Tokenizer.string(), STRING)
        t.addTokenType(":", COLON)
        t.addTokenType(",", COMMA)
        t.addTokenType("\\{", START_OBJECT)
        t.addTokenType("\\}", END_OBJECT)
        t.addTokenType("\\[", START_ARRAY)
        t.addTokenType("\\]", END_ARRAY)
        t.addTokenType(Tokenizer.keyword("null"), NULL)
        t.addTokenType(Tokenizer.keyword("true"), TRUE)
        t.addTokenType(Tokenizer.keyword("false"), FALSE)
        t.addTokenType(Tokenizer.REGEX_DOUBLE, DOUBLE) // hex/octal/binary not supported
        t.addTokenType(Tokenizer.REGEX_INT, DOUBLE) // hex/octal/binary not supported
        t
    }

    /**
     * Get a [ConfigSection] from a JSON Object string
     *
     * @param src The source string
     * @return a [ConfigSection] accurately represented by the given source string
     */
    @Synchronized
    override fun get(src: String): ConfigSection {
        tokenizer.src = src
        val token = tokenizer.next() ?: return ConfigSection()
        if (token.type != START_OBJECT) tokenizer.unexpected(token.match, "'{'")
        return getObject()
    }

    private fun getObject(): ConfigSection {
        val section = ConfigSection()
        var token = tokenizer.nextOrError()

        //if '}' don't add values
        if (token.type == END_OBJECT) return section

        while (true) {
            // we expect the next token to be a string value for the key
            if (token.type != STRING) tokenizer.unexpected(token.match, "a quoted string")
            val key = token.match.substring(1 until token.match.lastIndex)

            token = tokenizer.nextOrError()

            // we expect the next token after the key to be a colon
            if (token.type != COLON) tokenizer.unexpected(token.match, "':'")
            token = tokenizer.nextOrError()

            // we expect the next token after the colon to be a value of some sort
            section[key] = getValue(token)

            // we expect a comma to continue adding k/v pairs or '}' to return the object
            if (!tokenizer.shouldContinue(END_OBJECT, COMMA, "COMMA or END_OBJECT")) break
            token = tokenizer.nextOrError()
        }
        return section
    }

    private fun getArray(): ConfigSequence {
        val seq = ConfigSequence()
        var token = tokenizer.nextOrError()

        // if ']' don't add values
        if (token.type == END_ARRAY) return seq

        while (true) {
            //we expect the next token to be a value of some kind
            seq.add(getValue(token))

            //we expect a comma to continue adding values or ']' to return the array
            if (!tokenizer.shouldContinue(END_ARRAY, COMMA, "COMMA or END_ARRAY")) break
            token = tokenizer.nextOrError()
        }
        return seq
    }

    private fun getValue(token: Tokenizer.Token): Any? = when (token.type) {
        STRING -> token.match.substring(1 until token.match.lastIndex).unescape()
        DOUBLE -> ArgumentTypes.NUMBER(token.match)
        INT -> token.match.toInt()
        FALSE -> false
        TRUE -> true
        NULL -> null
        START_OBJECT -> getObject()
        START_ARRAY -> getArray()
        else -> tokenizer.unexpected(token.match)
    }

    override fun set(writer: FileWriter, section: ConfigSection): Boolean {
        return set(writer, DEF_INDENT_SIZE, 0, section)
    }

    /**
     * Write a [ConfigSection] to the given file writer in JSON format
     *
     * @param writer The file writer
     * @param indentSize The indenting size
     * @param indent The initial level of indentation
     * @param section The ConfigSection
     * @return `true` if no Exceptions occur
     */
    operator fun set(writer: FileWriter, indentSize: Int = DEF_INDENT_SIZE, indent: Int = 0, section: ConfigSection): Boolean =
            try {
                writer.write(stringify(section, indentSize, indent))
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }

}