package info.malignantshadow.api.util.arguments

import info.malignantshadow.api.util.aliases.checkAlias

/**
 * Represents an argument
 * @author Shad0w (Caleb Downs)
 */
open class Argument(
        val name: String,
        val desc: String?,
        val required: Boolean = false,
        val display: String = ""
) {

    init {
        name.checkAlias(true, true, true)
    }

    companion object Static {
        fun shouldUseDefault(input: String?): Boolean = input == null || input.isEmpty()
    }

    /**
     * Indicates whether the value of this Argument can be null
     */
    var canBeNull: Boolean = false

    /**
     * The default value of this argument
     */
    var def: Any? = null

    /**
     * The acceptable value types of this argument
     */
    var types: ArrayList<(String?) -> Any?> = arrayListOf(ArgumentTypes.STRING)

    /**
     * The display
     */
    val shownDisplay: String get() = if(display.isEmpty()) name else display

    /**
     * Construct an Argument
     *
     * @param name The name of this argument
     * @param desc The description/usage of this argument
     * @param required Whether this argument is required
     * @param display A display string to use instead of its name
     * @param init An extension function that is invoked when constructing this argument
     */
    constructor(
            name: String,
            desc: String?,
            required: Boolean = false,
            display: String = "",
            init: Argument.() -> Unit = {}
    ) : this(name, desc, required, display) {
        this.init()
    }

    /**
     * Specify whether the value of this Argument can be `null` when parsed
     *
     * @param canBeNull Whether the value can be `null`
     * @return this
     */
    fun thatMayBeNull(canBeNull: Boolean): Argument {
        this.canBeNull = canBeNull
        return this
    }

    /**
     * Specify the accepted value types for this argument.
     *
     * @param types The accepted argument types
     * @return this
     *
     * @see [ArgumentTypes]
     */
    fun withAcceptedTypes(vararg types: (String?) -> Any?): Argument {
        this.types = arrayListOf(*types)
        return this
    }

    /**
     * Get the value of this argument if it was given the specified input
     *
     * @param input The input
     * @return the value
     */
    fun getValue(input: String?): Any? {
        if (shouldUseDefault(input))
            return def

        types.forEach {
            val value: Any? = it(input)
            value.let { return value }
        }

        return null
    }

    /**
     * Add the type to this argument as an accepted argument type
     *
     * @see ArgumentTypes
     */
    operator fun ((String?) -> Any?).unaryPlus() {
        types.add(this)
    }

    /**
     * Add all of the given types as accepted argument types
     *
     * @see ArgumentTypes
     */
    operator fun Array<(String?) -> Any?>.unaryPlus() {
        types.addAll(this)
    }

}