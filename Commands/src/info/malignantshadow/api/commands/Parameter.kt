package info.malignantshadow.api.commands

import info.malignantshadow.api.util.parsing.ParameterType
import info.malignantshadow.api.util.parsing.ParameterTypes

// TODO: Parameters can have custom validators
open class Parameter(
        val name: String,
        val description: String,
        val usage: String?,
        val types: List<ParameterType<*>>,
        val nullable: Boolean,
        val def: Any?
) {

    val type = ParameterTypes.first(types)

    open val shownDisplay = if(usage?.isNotBlank() == true) usage else name

    fun getValueFrom(input: String?) = if(input == null) def else type.parse(input)

}