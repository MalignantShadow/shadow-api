package info.malignantshadow.api.util.arguments

class ParsedArgument(val arg: Argument, val input: String?, val value: Any?) {

    private var _defaultUsed = false

    val defaultUsed = _defaultUsed

    constructor(arg: Argument, input: String?): this(arg, input, arg.getValue(input)) {
        _defaultUsed = Argument.shouldUseDefault(input)
    }

}