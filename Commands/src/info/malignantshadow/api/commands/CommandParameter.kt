package info.malignantshadow.api.commands

import info.malignantshadow.api.util.parsing.StringTransformers

@CommandDsl
class CommandParameter(val name: String, val desc: String, var isRequired: Boolean = false) {

    var display: String = ""
    val shownDisplay get() = if(display.isBlank()) name else display
    val types = ArrayList<(String) -> Any?>()
    var def: Any? = null
    var isNullable = false

    fun shouldUseDefault(input: String?) = input == null || input.isBlank()

    fun typeOf(type: (String) -> Any?) {
        types.add(type)
    }

    fun typeOf(types: Iterable<(String) -> Any?>) {
        this.types.clear()
        this.types.addAll(types)
    }

    inline fun <reified E: Enum<E>> typeOf(caseSensitive: Boolean = false) {
        types.add(StringTransformers.enumValue<E>())
    }


    fun isNullable() { isNullable = true }
    fun isRequired() { isRequired = true }

    fun getValueFrom(input: String): Any? {
        if(shouldUseDefault(input) || types.isEmpty()) return def

        types.forEach {
            val value = it(input)
            if(value != null) return@getValueFrom value
        }

        return def
    }

}