package info.malignantshadow.api.util.selectors

class SelectorArgument(val name: String, val input: Array<String>) {

    override fun toString() = name + "=" + input.joinToString("|")

}