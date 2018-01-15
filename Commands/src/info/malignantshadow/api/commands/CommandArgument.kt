package info.malignantshadow.api.commands

@CommandDsl
class CommandArgument(val name: String, val desc: String, val isRequired: Boolean) {

    var display: String = ""
    val shownDisplay get() = if(display.isBlank()) name else display
    val types = ArrayList<(String) -> Any?>()
    var def: Any? = null
    var isNullable = false

    fun shouldUseDefault(input: String?) = input == null || input.isBlank()

    fun type(type: (String) -> Any?) {
        types.add(type)
    }

    fun isNullable() { isNullable = true }

    fun getValueFrom(input: String): Any? {
        if(shouldUseDefault(input) || types.isEmpty()) return def

        types.forEach {
            val value = it(input)
            if(value != null) return@getValueFrom value
        }

        return def
    }

}