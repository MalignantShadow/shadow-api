@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("unused")

package info.malignantshadow.api.util

/**
 * Indicates whether the string is equal to any of the given `tests`
 *
 * @param ignoreCase Whether to be case-sensitive
 * @param tests The test strings
 */
fun String?.equalsAny(ignoreCase: Boolean = false, vararg tests: String?): Boolean {
    for (s in tests) {
        if (this == null && s == null)
            return true
        else if (this == null || s == null)
            continue

        if (s.equals(s, ignoreCase)) return true
    }

    return false
}

/**
 * Shortcut for String.repeat(n)
 *
 * @param n The amount of times to repeat the String
 */
operator fun String.times(n: Int) = this.repeat(n)

/**
 * Indent a string
 *
 * @param indentSize The size of the indent
 * @param indent The level of indentation
 * @param indentChar The indentation character
 */
@JvmOverloads
fun String.indent(indentSize: Int = 2, indent: Int = 1, indentChar: Char = ' ') =
        indentChar.toString().repeat(indentSize).repeat(indent) + this

/**
 * Escape newlines and quoting characters
 *
 * @return a new escaped string
 */
fun String.escape() =
        if (isEmpty()) this
        else {
            var str = ""
            forEach {
                str += when (it) {
                    '\n' -> "\\n"
                    '\r' -> "\\r"
                    '\t' -> "\\t"
                    '\b' -> "\\b"
                    '\\' -> "\\\\"
                    '\'' -> "\\'"
                    '"' -> "\\\""
                    else -> it
                }
            }
            str
        }

/**
 * Unescape newlines and quoting characters
 */
fun String.unescape(): String {
    return if (isEmpty()) this
    else {
        var str = ""
        var index = 0
        while (index <= this.lastIndex) {
            val c = this[index++]
            when (c) {
                '\\' -> {
                    when (this.getOrNull(index++)) {
                        null -> str += "\\"
                        'r' -> str += "\r"
                        'n' -> str += "\n"
                        't' -> str += "\t"
                        'b' -> str += "\b"
                        'u' ->
                            if (this.length - index >= 4) {
                                str += this.substring(index, index + 4).toUpperCase().toInt(16).toChar()
                                index += 4
                            } else {
                                str += "\\u"
                            }
                        '\\' -> str += "\\\\"
                        '\'' -> str += "'"
                        '"' -> str += "\""
                    }
                }
                else -> {
                    str += c
                }
            }
        }
        str
    }
}

fun String.toProperCase(): String {
    if (isEmpty())
        return ""
    val unimportant = arrayOf("a", "an", "and", "but", "is", "are", "for", "nor", "of", "or", "so", "the", "to", "yet", "by")
    val split = split("\\s+")
    var result = ""
    for (i in split.indices) {
        val word = split[i]
        result +=
                //first and last words are always capital
                (if (word !in unimportant || i == 0 || i == split.size - 1)
                    word.capitalize()
                else
                    word.toLowerCase()) + " "
    }
    return result.trim { it <= ' ' }
}