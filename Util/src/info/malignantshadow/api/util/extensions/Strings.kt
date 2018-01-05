@file:JvmMultifileClass
@file:JvmName("Extensions")
@file:Suppress("unused")

package info.malignantshadow.api.util.extensions

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

@JvmOverloads
fun String.indent(indentSize: Int = 2, indent: Int = 1, indentChar: Char = ' ') =
        indentChar.toString().repeat(indentSize).repeat(indent) + this

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

fun String.unescape(): String {
    return if (isEmpty()) this
    else {
        var str = ""
        var index = 0
        while (index <= this.lastIndex) {
            val c = this[index++]
            when (c) {
                '\\' -> {
                    when (this[index++]) {
                        'r' -> str += "\r"
                        'n' -> str += "\n"
                        't' -> str += "\t"
                        'b' -> str += "\b"
                        '\\' -> str += "\\"
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