package info.malignantshadow.api.commands.build.attributes

import info.malignantshadow.api.util.parsing.ParameterType

interface Parsable {

    /**
     * Adds a [ParameterType] to this object.
     */
    fun type(type: ParameterType<*>)

    /**
     * Adds multiple [ParameterType]s to this object.
     * @param types The ParameterTypes to add
     */
    fun types(types: Iterable<ParameterType<*>>) =
            types.forEach { type(it) }

    /**
     * Adds multiple [ParameterType]s to this object.
     * @param first The first type
     * @param second The second type
     * @param others Other types to add
     */
    fun types(first: ParameterType<*>, second: ParameterType<*>, vararg others: ParameterType<*>) =
            types(listOf(first, second, *others))

    /**
     * Sets whether this object can have a nullable value. An error will occur if this value is `false`
     * and the parsed value from the given input is `null`.
     */
    fun nullableValue(nullable: Boolean = true)

    /**
     * Sets the default value of this object. It is pointless to call this function on a parameter that is
     * required to have input.
     */
    fun defaultValue(def: Any?)

    /**
     * Sets the usage string of this object. For parameters, this is shown instead of the parameter's name, e.g.:
     * * `[entity: string|int]`
     * * `<entity: String|int>`
     *
     * For options, this will be shown as the name of the flag's value, e.g.:
     * * `-f VALUE`
     * * `--flag=VALUE`
     */
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
