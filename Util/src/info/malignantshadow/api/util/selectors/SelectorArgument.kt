package info.malignantshadow.api.util.selectors

class SelectorArgument(val name: String, val op: Int, val input: List<String>) {

    companion object {

        const val OP_EQ = 0
        const val OP_NOTEQ = 1
        const val OP_LESS_THAN = 2
        const val OP_LTEQ = 3
        const val OP_GREATER_THAN = 4
        const val OP_GT_EQ = 4

    }

    /**
     * Parse the input
     *
     * @param parse The function used to parse each input
     */
    fun <T> getValues(parse: (String) -> T) = input.map(parse)

}