package info.malignantshadow.api.util.parsing

class ParserException(message: String, val parser: Parser) : RuntimeException(message) {

    override val message: String
        get() =
                "${super.message} at character ${parser.index} (${parser.line}:${parser.lineIndex})"

}