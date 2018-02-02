package info.malignantshadow.api.commands.build.attributes

import info.malignantshadow.api.util.parsing.ParameterType

interface Parsable {

    fun type(type: ParameterType<*>)

    fun types(types: Iterable<ParameterType<*>>) =
            types.forEach { type(it) }

    fun types(first: ParameterType<*>, second: ParameterType<*>, vararg others: ParameterType<*>) =
            types(listOf(first, second, *others))

    fun nullableValue(nullable: Boolean = true)

    fun defaultValue(def: Any?)

    fun usage(usage: String)

}

class SimpleParsable: Parsable {

    val types = ArrayList<ParameterType<*>>()
    var nullable = false
        private set
    var def: Any? = null
        private set
    var usage : String? = null
        private set

    override fun type(type: ParameterType<*>) {
        types.add(type)
    }

    override fun nullableValue(nullable: Boolean) {
        this.nullable = nullable
    }

    override fun defaultValue(def: Any?) {
        this.def = def
    }

    override fun usage(usage: String) {
        this.usage = usage
    }

}
