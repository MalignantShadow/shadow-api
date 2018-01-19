package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandParameter
import info.malignantshadow.api.util.parsing.ParameterType

/**
 * A builder for a command parameter. Created by calling [param][CommandSpecBuilder.param] from a command builder.
 */
@CommandDsl
class CommandParameterBuilder(

        /**
         * The name, used in retrieving input and values from
         * [CommandContext][info.malignantshadow.api.commands.dispatch.CommandContext] objects.
         */
        val name: String,

        /**
         * The description
         */
        val desc: String,

        /**
         * The display, this value should be shown instead of the parameter's name in help listings
         */
        val display: String
) {

    private var isRequired: Boolean = false
    private var isNullable: Boolean = false
    private var types = ArrayList<(String) -> Any?>()
    private var def: Any? = null

    init {
        if (name.startsWith("-"))
            require(Regex("\\s") !in name) { "flags cannot contain whitespace in their name" }
    }

    /**
     * Add an input type to this parameter.
     *
     * @param type The type
     */
    fun typeOf(type: (String) -> Any?) {
        types.add(type)
    }

    /**
     * Add an input type to this parameter that parses the argument as an enum value.
     * Uses [ParameterType.enumValue]
     *
     * @param E The enum
     * @param caseSensitive Whether the name matching is case-sensitive, `false` by default
     */
    inline fun <reified E: Enum<E>> typeOf(caseSensitive: Boolean = false) {
        typeOf(ParameterType.enumValue<E>(caseSensitive))
    }

    /**
     * Add multiple input types to this parameter.
     *
     * @param types The types
     */
    fun types(types: Iterable<(String) -> Any?>) {
        this.types.addAll(types)
    }

    /**
     * Add multiple input types to this parameter.
     *
     * @param types The types
     */
    fun types(vararg types: (String) -> Any?) = types(listOf(*types))

    /**
     * Set the input types for this parameter by clearing any previously added types and
     * adding the supplied types.
     *
     * @param types The new types
     */
    fun setTypes(types: Iterable<(String) -> Any?>) {
        this.types.clear()
        types(types)
    }

    /**
     * Set the input types for this parameter by clearing any previously added types and
     * adding the supplied types.
     *
     * @param types The new types
     */
    fun setTypes(vararg types: (String) -> Any?) = types(listOf(*types))

    /**
     * Indicate whether this parameter is required to have a value.
     *
     * @param required Whether this parameter is required. `true` by default
     */
    fun isRequired(required: Boolean = true) {
        isRequired = required
    }

    /**
     * Indicate whether this parameter can have a `null` value.
     *
     * @param nullable Whether this parameter is required. `true` by default
     */
    fun isNullable(nullable: Boolean = true) {
        isNullable = nullable
    }

    /**
     * Specify the default value for this parameter. Note that it is pointless to give a
     * required parameter a default value.
     *
     * @param value The default value
     */
    fun def(value: Any) {
        def = value
    }

    internal fun build() =
            CommandParameter(
                    name,
                    desc,
                    display,
                    if(!name.startsWith("-") && types.isEmpty()) listOf(ParameterType.STRING) else types,
                    isRequired,
                    isNullable,
                    def
            )

}