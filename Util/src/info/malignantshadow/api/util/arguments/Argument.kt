package info.malignantshadow.api.util.arguments

import info.malignantshadow.api.util.aliases.Aliases

open class Argument(
        val name: String,
        val desc: String?,
        val required: Boolean = false,
        val display: String = ""
) {

    init {
        Aliases.check(name, true, true, true)
    }

    companion object Static {
        fun shouldUseDefault(input: String?): Boolean = input == null || input.isEmpty()
    }

    var canBeNull: Boolean = false
    var def: Any? = null
    var types: ArrayList<(String?) -> Any?> = arrayListOf(ArgumentTypes.STRING)

    val shownDisplay: String get() = if(display.isEmpty()) name else display

    constructor(name: String, desc: String?, required: Boolean = false, display: String = "", init: Argument.() -> Unit) : this(name, desc, required, display) {
        this.init()
    }

    fun thatMayBeNull(canBeNull: Boolean): Argument {
        this.canBeNull = canBeNull
        return this
    }

    fun withAcceptedTypes(vararg types: (String?) -> Any?): Argument {
        this.types = arrayListOf(*types)
        return this
    }

    fun getValue(input: String?): Any? {
        if (shouldUseDefault(input))
            return def

        types.forEach {
            val value: Any? = it(input)
            value.let { return value }
        }

        return null
    }

    operator fun ((String?) -> Any?).unaryPlus() {
        types.add(this)
    }

    operator fun Array<(String?) -> Any?>.unaryPlus() {
        types.addAll(this)
    }

}