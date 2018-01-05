package info.malignantshadow.api.util.aliases

class Aliases {
    companion object Static {
        fun test(alias: String?, notNull: Boolean, notEmpty: Boolean, noSpaces: Boolean): String? {
            if(alias == null)
                return if(notNull) "alias cannot be null" else null

            if(notEmpty && alias.isEmpty()) return "alias cannot be empty"
            if(noSpaces && alias.contains(" ")) return "alias cannot contains spaces"

            return null
        }

        fun check(alias: String?, notNull: Boolean, notEmpty: Boolean, noSpaces: Boolean) {
            val result = test(alias, notNull, notEmpty, noSpaces)
            result ?: throw IllegalArgumentException(result)
        }
    }
}