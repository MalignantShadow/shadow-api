package info.malignantshadow.api.util.aliases

fun String?.testAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean): String? {
    if (this == null)
        return if (notNull) "alias cannot be null" else null

    if (notEmpty && isEmpty()) return "alias cannot be empty"
    if (noWhitespace && contains(Regex("\\s+"))) return "alias cannot contain whitespace"

    return null
}

fun String?.isValidAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean): Boolean =
        testAlias(notNull, notEmpty, noWhitespace) != null

fun String?.checkAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean) {
    val result = testAlias(notNull, notEmpty, noWhitespace)
    require(result == null) { result!! }
}