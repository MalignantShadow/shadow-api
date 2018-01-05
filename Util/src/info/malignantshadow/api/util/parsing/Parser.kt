package info.malignantshadow.api.util.parsing

import info.malignantshadow.api.util.arguments.ArgumentTypes

open class Parser(val src: String) {

    private var _line = 1
    val line get() = _line

    private var _lineIndex = 0
    val lineIndex get() = _lineIndex

    private var _index = 0
    val index get() = _index

    private var _eof = false
    val eof get() = _eof

    val current: Char
        get() {
            if (_eof || _index >= src.length) {
                _eof = true
                return 0.toChar()
            }
            return src[_index]
        }

    fun next(): Char {
        _index++
        _lineIndex++
        val c = current
        if (c == '\n') {
            _line++
            _lineIndex = 0
        }
        return c
    }

    fun next(length: Int): String {
        var str = ""
        for (i in 0 until length) {
            val n = next()
            if (_eof) break
            str += n
        }
        return str
    }

    private fun expect(c: Char, d: Char) = c == d
    fun expectCurrent(c: Char) = expect(current, c)
    fun expectNext(c: Char) = expect(next(), c)

    fun expect(next: String?): Boolean {
        if (next == null) return _eof
        val nextChars = next(next.length)
        if(nextChars == next) {
            next() // done here in case of early eof
            return true
        }
        return false
    }

    fun Char.isWhitespace(newline: Boolean): Boolean {
        if (newline) return this.isWhitespace()
        return !(this == '\n' || this == '\r')
    }

    @JvmOverloads
    fun skipWhitespace(newline: Boolean = true): String {
        var str = ""
        while (true) {
            val c = current
            if (c.isWhitespace(newline)) {
                str += c
                next()
            }
            else break
        }
        return str
    }

    fun nextClean(): Char {
        val c = current
        if (!c.isWhitespace()) return c
        skipWhitespace()
        return current
    }

    fun untilNewline() = until('\n')
    fun until(until: Char) = until { it == until }

    fun until(predicate: (Char) -> Boolean): String {
        var c = current
        var str = "" + c
        while (true) {
            if (predicate(c)) break
            c = next()
            str += c
        }
        return str
    }

    @JvmOverloads
    fun untilWhitespace(newline: Boolean = true) = until { it.isWhitespace(newline) }

    @JvmOverloads
    fun readString(quote: Char, newline: Boolean = false): String {
        var c = current
        var str = ""
        while (true) {
            if (_eof) break

            if (c == quote) {
                next()
                break
            }

            when (c) {
                Character.MIN_VALUE -> throw ParserException("Unexpected EOF", this)
                '\r', '\n' -> {
                    if (!newline) throw ParserException("Unterminated string", this)
                    str += c
                }
                '\\' -> {
                    val d = next()
                    str += when (d) {
                        'b' -> '\b'
                        't' -> '\t'
                        'n' -> '\n'
                        'r' -> '\r'
                        '\\', '\'', '"' -> d
                        else -> throw ParserException("Unsupported escape", this)
                    }
                }
                else -> str += c
            }
            c = next()
        }
        return str
    }

    private fun Char.isQuote() = this == '\'' || this == '"'

    private fun Char.isNumberPart() =
            isDigit() || this == '.' || this == '-' || this == '+' || toUpperCase() == 'E' || this == '_'
    // adopt Kotlin style of underscores to makes numbers easier to read

    fun readNumber(): Number? {
        var str = "" + current
        var c = next()
        while (true) {
            if (_eof) break
            if (c.isNumberPart()) str += c else break
            c = next()
        }
        return ArgumentTypes.NUMBER(str.replace("_", ""))
    }

    @JvmOverloads
    fun readLiteral(newlineForStrings: Boolean = false): Any? {
        val c = current
        return when {
            c.isQuote() -> {
                next()
                readString(c, newlineForStrings)
            }
            c == 't' -> {
                if (!expect("rue")) throw ParserException("Expected 'rue' to complete Boolean literal", this)
                true
            }
            c == 'f' -> {
                if (!expect("alse")) throw ParserException("Expected 'alse' to complete Boolean literal", this)
                false
            }
            c == 'n' -> {
                if (!expect("ull")) throw ParserException("Expected 'ull' to complete null literal", this)
                null
            }
            c.isNumberPart() -> readNumber() ?: throw ParserException("Could not parse Number", this)
            else -> throw ParserException("Unexpected character '$c'", this)
        }
    }

}