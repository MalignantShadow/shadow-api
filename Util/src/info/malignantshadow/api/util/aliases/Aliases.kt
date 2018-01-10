@file:Suppress("UNUSED")

package info.malignantshadow.api.util.aliases

/**
 * Test whether a String is suitable for being an alias
 *
 * @param notNull Whether the alias can be `null`
 * @param notEmpty Whether the alias can be an empty string
 * @param noWhitespace Whether the alias can contain whitespace characters
 * @return `null` if the string is a suitable alias or a String to be passed
 * to an [IllegalArgumentException]
 */
fun String?.testAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean): String? {
    if (this == null)
        return if (notNull) "alias cannot be null" else null

    if (notEmpty && isEmpty()) return "alias cannot be empty"
    if (noWhitespace && contains(Regex("\\s+"))) return "alias cannot contain whitespace"

    return null
}

/**
 * Indicates whether a String is a suitable alias or not
 *
 * @param notNull Whether the alias can be `null`
 * @param notEmpty Whether the alias can be an empty string
 * @param noWhitespace Whether the alias can contain whitespace characters
 * @return true if the string is a suitable alias
 *
 */
fun String?.isValidAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean): Boolean =
        testAlias(notNull, notEmpty, noWhitespace) != null

/**
 * Check whether a String is a suitable alias and throw an [IllegalArgumentException] if it isn't
 *
 * @param notNull Whether the alias can be `null`
 * @param notEmpty Whether the alias can be an empty string
 * @param noWhitespace Whether the alias can contain whitespace characters
 */
fun String?.checkAlias(notNull: Boolean, notEmpty: Boolean, noWhitespace: Boolean) {
    val result = testAlias(notNull, notEmpty, noWhitespace)
    require(result == null) { result!! }
}