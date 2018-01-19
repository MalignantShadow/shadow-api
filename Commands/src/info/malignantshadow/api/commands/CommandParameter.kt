package info.malignantshadow.api.commands

import info.malignantshadow.api.util.parsing.ParameterType

class CommandParameter(
        val name: String,
        val desc: String,
        val display: String,
        val types: List<(String) -> Any?>,
        val isRequired: Boolean,
        val isNullable: Boolean,
        val def: Any?
) {

    init {
        check(!isNullable || def != null) { "Parameter requires a non-null value but the default value is null" }
    }

    val isFlag = name.startsWith("-")
    val isLongFlag = isFlag && name.length > 2
    val fullName = if(isFlag && isLongFlag) "-$name" else name

    fun getValueFrom(input: String) =
            if(input.isBlank() || types.isEmpty()) def else ParameterType.firstMatch(types)(input)

}