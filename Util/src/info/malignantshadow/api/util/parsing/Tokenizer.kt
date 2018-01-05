@file:Suppress("unused")

package info.malignantshadow.api.util.parsing

import java.util.regex.Pattern

/**
 * A class to aid the parsing of complex strings, such as data formats or even
 * programming languages. The programmer may add any number of token types, and they
 * will be attempted to match in the order they were added.
 *
 * @author Shad0w (Caleb Downs)
 */
open class Tokenizer(src: String, val skipWhitespace: Boolean = true) {

    companion object {

        /**
         * An unsigned Int. Matches 1 or more decimal characters (0-9)
         */
        const val REGEX_UINT = "\\d+\\b"

        /**
         * An (optionally) signed Int. Matches 1 or more decimal characters (0-9)
         * proceeded by an optional sign (+ or -)
         */
        @JvmStatic
        val REGEX_INT = "[+-]?$REGEX_UINT"

        /**
         * An unsigned Double. Matches any of the following number formats:
         * * .#
         * * .#e#
         * * #.#
         * * #.#e#
         */
        const val REGEX_UDOUBLE = "\\d*(\\.\\d+([eE]\\d+)?)\\b"

        /**
         * The same as a [REGEX_UDOUBLE], but proceeded by an optional sign (+ or -)
         */
        @JvmStatic
        val REGEX_DOUBLE = "[+-]?$REGEX_UDOUBLE"

        /**
         * An identifier that can contain alphanumeric characters and underscores. The identifier
         * will not match if it begins with a number, however.
         */
        const val REGEX_IDENTIFIER = "[a-zA-Z_][\\w_]*"

        /**
         * The same as [REGEX_IDENTIFIER], but it can also contain dashes (-)
         */
        const val REGEX_IDENTIFIER_WITH_DASHES = "[a-zA-Z_-][\\w_\\-]*"

        /**
         * The same as [REGEX_IDENTIFIER_WITH_DASHES], but it cannot start or end with a dash (-)
         */
        const val REGEX_IDENTIFIER_WITHOUT_DASHES_ON_ENDS = "[a-zA-z_]([^\\-\\W]|-[a-zA-Z_])*"

        /**
         * Constructs a regex that matches a string that can contain any of the allowed characters
         * and is enclosed with one of the supplied enclosing `chars`
         *
         * @param chars The enclosing characters
         * @param allowedChars The allowed characters
         * @return `"([${chars}])[${allowedChars}]*\2"`
         */
        @JvmStatic
        fun enclosedWithChar(chars: String, allowedChars: String): String {
            require(!chars.isEmpty()) { "'chars' cannot be empty" }
            require(!allowedChars.isEmpty()) { "'allowedChars' cannot be empty" }
            return "([$chars])[$allowedChars]*\\2"
        }

        /**
         * Constructs a regex that matches a string that can contain any of the allowed characters
         * and is enclosed with the given enclosing `string`
         *
         * @param string The enclosing String
         * @param allowedChars The allowed characters
         * @return `"$string[${allowedChars}]*$string"`
         */
        @JvmStatic
        fun enclosedWith(string: String, allowedChars: String): String {
            require(!string.isEmpty()) { "'string' cannot be empty" }
            require(!allowedChars.isEmpty()) { "'allowedChars' cannot be empty" }
            return "$string[$allowedChars]*$string"
        }

        /**
         * Constructs a regex that matches a string literal in most languages. The following
         * escapes are supported by the returned regex:
         * * \n
         * * \t
         * * \r
         * * \f
         * * \' (if `single` is true)
         * * \" (if `double` is true)
         *
         * @param single Whether single quotes (') are acceptable
         * @param double Whether double quotes (") are acceptable
         * @param newlines Whether literal newline characters (`\n`, `\r`, `\f`) are accepted
         * @return A regex that matches a string literal with the given options
         */
        @JvmStatic
        @JvmOverloads
        fun string(single: Boolean = true, double: Boolean = true, newlines: Boolean = false): String {
            require(single || double) { "'single' or 'double' must be true" }
            val chars = (if (single) "'" else "") + (if (double) "\"" else "")
            return "([$chars])(?:[^\\\\$chars${if (!newlines) "\\n\\r\\f" else ""}]|\\\\[${chars}ntrf])*\\2(?![^\\s])"
        }

        /**
         * Constructs a regex that matches a keyword. It is suggested that the returned
         * regex be used to denote keywords of the same category.
         *
         * @param keywords The keywords to accept
         * @return `"(keyword1|keyword2|..|keywordN)\b"`
         */
        @JvmStatic
        fun keyword(vararg keywords: String): String {
            require(!keywords.isEmpty()) { "keywords cannot be empty" }
            return keywords.joinToString("|", "(", ")\\b")
        }

    }

    /**
     * Represents a Token, which contains an Int representing its type and a String representing
     * the match that was found
     */
    data class Token(

            /**
             * The type of this Token
             */
            val type: Int,

            /**
             * The match of this Token
             */
            val match: String
    )

    private data class TokenInfo(val pattern: Pattern, val type: Int, val ignore: Boolean = false)

    private val _patterns: ArrayList<TokenInfo> = ArrayList()

    private val _tokens: ArrayList<Token> = ArrayList()


    /**
     * A copy of the token list
     */
    val tokens get() = _tokens.toList()

    private var _workingSrc = src

    /**
     * The source string for this Tokenizer to use.
     *
     * Setting this value will clear previous tokens.
     */
    open var src = src
        set(value) {
            _workingSrc = value
            _tokens.clear()
            field = value
        }

    /**
     * Add a Token type to this Tokenizer.
     *
     * Note that the supplied string is wrapped in a group: ^(). If the programmer
     * needs to use a numerical reference to a group, they should start with \2 onwards.
     *
     * @param regex The regex to use
     * @param type The type. This value should be used by the programmer later to determine
     * @param ignore Whether a Token of this type should be skipped (useful for comments).
     * a Token's type
     */
    @JvmOverloads
    fun addTokenType(regex: String, type: Int, ignore: Boolean = false) {
        _patterns.add(TokenInfo(Pattern.compile("^($regex)"), type, ignore))
    }

    /**
     * Get the next Token. If the end of the string was reached, this function will return
     * `null`. If no Token could be found, an exception is thrown
     *
     * @param skipWhitespace whether or not whitespace should be skipped. Defaults to the same
     * value used when constructing this Tokenizer.
     */
    @JvmOverloads
    fun next(skipWhitespace: Boolean = this.skipWhitespace): Token? {
        for ((pattern, type, ignore) in _patterns) {
            if (_workingSrc.isEmpty()) return null
            val m = pattern.matcher(_workingSrc)
            if (m.find()) {
                val token = Token(type, m.group())
                val str = _workingSrc.substring(token.match.length)
                _workingSrc = if (skipWhitespace) str.trim() else str
                _tokens.add(token)

                if (ignore) return next()

                return token
            }
        }
        error()
    }

    /**
     * Get the next Token, or throw an exception if the end of the string has been reached
     *
     * @param message The message to pass to the exception.
     */
    @JvmOverloads
    fun nextOrError(message: String = "Unexpected EOF") = next() ?: error(message)

    /**
     * Throw an exception explaining that the given match was unexpected
     *
     * @param match The unexpected match
     */
    fun unexpected(match: String): Nothing =
            error("Unexpected token '$match'")

    /**
     * Throw an exception explaining that the given match was unexpected, and that something
     * else was expected instead
     *
     * @param match The unexpected match
     * @param expected What was expected to be found
     */
    fun unexpected(match: String, expected: String): Nothing =
            error("Unexpected token '$match', expected $expected")

    /**
     * Test whether parsing should continue a loop based on the result of the next Token.
     * This function is typically used for parsing collection types
     *
     * @param end If the next Token is this type, return false
     * @param continueType If the next Token is this type, return true
     * @param expected A String to pass to [unexpected] if neither of the above types were found
     * @return `true` if the loop should continue
     */
    fun shouldContinue(end: Int, continueType: Int, expected: String): Boolean {
        val token = nextOrError()
        return when (token.type) {
            end -> false
            continueType -> true
            else -> unexpected(token.match, expected)
        }
    }

    /**
     * Returns a Token only if the supplied string completely matches any of the patterns
     * in this Tokenizer (more specifically, the match length is equivalent to the length of
     * the supplied string)
     *
     * @param string The string to test
     * @return a Token if a match was found, `null` otherwise
     */
    fun independent(string: String): Token? {
        for ((pattern, type) in _patterns) {
            val m = pattern.matcher(string)
            if (m.find()) {
                val match = m.group()
                if (match.length != string.length) continue
                return Token(type, m.group())
            }
        }
        return null
    }

    /**
     * Throw a [TokenizerException] with the given message
     */
    fun error(message: String = "Unexpected character"): Nothing = throw TokenizerException(message, _workingSrc)

}