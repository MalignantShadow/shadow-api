package info.malignantshadow.api.util.parsing

/**
 * A class to aid in the tokenization of a complex identifier or variable. Such a token follows typical
 * programming idioms:
 * * `varName`
 * * `varname.another_var`
 * * `varname[another_var]`
 * * `varname["another_var"]`
 * * `varname[0]`
 * Note that the identifier type used is [Tokenizer.REGEX_IDENTIFIER]
 * @author Shad0w (Caleb Downs)
 */
class VariableExpressionTokenizer(
        src: String,
        identifierType: Int,
        dotType: Int,
        stringType: Int,
        openType: Int,
        closeType: Int,
        singleQuotes: Boolean = true,
        doubleQuotes: Boolean = true
) : Tokenizer(src) {

    companion object {

        @JvmStatic
        fun regex(singleQuotes: Boolean = true, doubleQuotes: Boolean  = true): String {
            val identifier = REGEX_IDENTIFIER
            val string = string(singleQuotes, doubleQuotes)
            val int = REGEX_UINT
            return "$identifier(\\.$identifier|\\[($identifier|$string|$int)])*"
        }

    }

    val regex = Regex(Companion.regex(singleQuotes, doubleQuotes))

    init {
        addTokenType(REGEX_IDENTIFIER, identifierType)
        addTokenType("\\.", dotType)
        addTokenType(Tokenizer.string(singleQuotes, doubleQuotes), stringType)
        addTokenType("\\[", openType)
        addTokenType("\\]", closeType)
    }

    override var src: String
        get() = super.src
        set(value) {
            require(value.matches(regex)) { "value does not match constructed regex" }
            super.src = value
        }

}