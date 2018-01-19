package info.malignantshadow.api.commands.build

import info.malignantshadow.api.commands.CommandDsl
import info.malignantshadow.api.commands.CommandParameter
import info.malignantshadow.api.util.parsing.ParameterType

@CommandDsl
class CommandParameterBuilder(val name: String, val desc: String, val display: String) {

    private var isRequired: Boolean = false
    private var isNullable: Boolean = false
    private var types = ArrayList<(String) -> Any?>()
    private var def: Any? = null

    init {
        if (name.startsWith("-"))
            require(Regex("\\s") !in name) { "flags cannot contain whitespace in their name" }
    }

    fun typeOf(type: (String) -> Any?) {
        types.add(type)
    }

    inline fun <reified E: Enum<E>> typeOf(caseSensitive: Boolean = false) {
        typeOf(ParameterType.enumValue<E>(caseSensitive))
    }

    fun types(types: Iterable<(String) -> Any?>) {
        this.types.addAll(types)
    }

    fun setTypes(types: Iterable<(String) -> Any?>) {
        this.types.clear()
        types(types)
    }

    fun isRequired(required: Boolean = true) {
        isRequired = required
    }

    fun isNullable(nullable: Boolean = true) {
        isNullable = nullable
    }

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