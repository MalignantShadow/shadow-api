package info.malignantshadow.api.util.arguments

/**
 * Represents an [Argument] that has been parsed
 */
class ParsedArgument(

        /**
         * The argument
         */
        val arg: Argument,

        /**
         * The input string
         */
        val input: String?,

        /**
         * The parsed value
         */
        val value: Any?
) {

    private var _defaultUsed = false

    /**
     * Indicates whether the default value was used
     */
    val defaultUsed = _defaultUsed

    /**
     * Constructs a ParsedArgument using the given [Argument] and input string by parsing the input
     *
     * @param arg The argument
     * @param input The input string for this argument
     */
    constructor(arg: Argument, input: String?): this(arg, input, arg.getValue(input)) {
        _defaultUsed = Argument.shouldUseDefault(input)
    }

}