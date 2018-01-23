package info.malignantshadow.api.util.selectors

import info.malignantshadow.api.util.parsing.Tokenizer

class Selector(val name: String, var args: List<SelectorArgument>) : Iterable<SelectorArgument> {

    companion object {

        val REGEX_PREFIXES = "[~!@#$]"
        val REGEX_NAME = Tokenizer.REGEX_IDENTIFIER
        val REGEX_VALUE = "(\\S+|${Tokenizer.string()})"
        val REGEX_OP = "([<>=]|([!<>]=))"
        val REGEX_PAIR = "$REGEX_NAME$REGEX_OP($REGEX_VALUE(\\|$REGEX_VALUE)*)"
        val REGEX = Regex("$REGEX_PREFIXES$REGEX_NAME(\\[$REGEX_PAIR(,$REGEX_PAIR)*])?")

        private val T_NAME = 0
        private val T_OP = 1
        private val T_VALUE = 2
        private val T_NEXT = 3
        private val T_END = 4

        @JvmStatic
        fun compile(string: String): Selector {
            require(string.matches(REGEX)) { "Given string does not match the regex ${REGEX.pattern}" }
            if ("[" !in string) return Selector(string, emptyList())
            val name = string.substring(0..string.indexOf("["))
            val tokenizer = Tokenizer(string.substring(name.length + 1)) // skip '['
            tokenizer.addTokenType(REGEX_NAME, T_NAME)
            tokenizer.addTokenType(REGEX_OP, T_OP)
            tokenizer.addTokenType(REGEX_VALUE, T_VALUE)
            tokenizer.addTokenType(",", T_NEXT)
            tokenizer.addTokenType("]", T_END)
            var token = tokenizer.next()!! //according to the regex, at least ']' is needed
            val args = ArrayList<SelectorArgument>()

            // Token types rarely check because the regex was tested against first
            while(true) {
                if(token.type == T_END)
                    return Selector(name, args)

                // must be name
                val paramName = token.match

                // must be operator
                val op = when(tokenizer.next()!!.match) {
                    "!="  -> SelectorArgument.OP_NOTEQ
                    ">" -> SelectorArgument.OP_GREATER_THAN
                    ">=" -> SelectorArgument.OP_GT_EQ
                    "<" -> SelectorArgument.OP_LESS_THAN
                    "<=" -> SelectorArgument.OP_LTEQ
                    else -> SelectorArgument.OP_EQ
                }

                //must be value/input
                val input = tokenizer.next()!!.match.split(Regex(""))
                args.add(SelectorArgument(paramName, op, input))

                token = tokenizer.next()!!
            }
        }

    }

    override fun iterator() = args.iterator()

}