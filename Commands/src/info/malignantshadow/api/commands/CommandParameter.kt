package info.malignantshadow.api.commands

import info.malignantshadow.api.util.parsing.ParameterType

class CommandParameter(

        /**
         * The name. A value starting with a dash (`-`) indicates that this parameter is
         * considered a flag. If there is more than one character after the dash, it is considered
         * a 'long' flag, one character marks it as a 'short' flag
         */
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
    val shownDisplay = when {
        isFlag -> fullName
        display.isBlank() -> name
        else -> display
    }

    fun getValueFrom(input: String) =
            if(input.isBlank() || types.isEmpty()) def else ParameterType.firstMatch(types)(input)

}