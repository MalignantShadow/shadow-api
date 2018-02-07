package info.malignantshadow.api.commands

import info.malignantshadow.api.util.aliases.Aliasable
import info.malignantshadow.api.util.parsing.ParameterType

class Option(
        name: String,
        override val aliases: List<String>,
        description: String,
        usage: String?,
        types: List<ParameterType<*>>,
        nullable: Boolean,
        def: Any?,
        val isRequired: Boolean,
        val requiredIf: List<String>,
        val requiredUnless: List<String>
) : Parameter(name, description, usage, types, nullable, def), Aliasable {

    companion object {

        fun getDisplay(alias: String) = if (alias.length > 1) "--$alias" else "-$alias"

    }

    val isLong = name.length > 1
    val requiresPresenceOnly = types.isEmpty()
    override val shownDisplay = getDisplay(name)

    val allAliases = listOf(name) + aliases

    val computedUsage = computeUsage(usage ?: "VALUE")

    fun computeUsage(valueString: String) =
            allAliases.sortedWith(Comparator { a, b ->
                when {
                    a.length == 1 && b.length > 1 -> -1
                    a.length > 1 && b.length == 1 -> 1
                    else -> a.compareTo(b)
                }
            }).mapIndexed { index, it ->
                if (!requiresPresenceOnly && index == allAliases.lastIndex)
                    if (it.length > 1) "${getDisplay(it)}=$valueString"
                    else "${getDisplay(it)} $valueString"
                else getDisplay(it)
            }.joinToString()


    fun isRequired(others: List<Option>): Boolean {
        if (requiresPresenceOnly) return false
        if (isRequired) return true
        if (requiredUnless.isNotEmpty() && others.any { it.name in requiredUnless }) return false
        if (requiredIf.isNotEmpty() && others.any { it.name in requiredIf }) return true
        return false
    }

}