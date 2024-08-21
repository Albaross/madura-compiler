package org.maduralang.lexer

fun isWhitespace(c: Char): Boolean = when (c) {
    ' ', in '\t'..'\r' -> true
    else -> false
}

fun isLetter(c: Char): Boolean = when (c) {
    in 'a'..'z', in 'A'..'Z' -> true
    else -> false
}

fun isDigit(c: Char): Boolean = when (c) {
    in '0'..'9' -> true
    else -> false
}

fun isDigitOrSeparator(c: Char): Boolean = when (c) {
    in '0'..'9', '_' -> true
    else -> false
}

fun isHexDigit(c: Char): Boolean = when (c) {
    in '0'..'9', in 'a'..'f', in 'A'..'F', '_' -> true
    else -> false
}

fun isHexDigitOrSeparator(c: Char): Boolean = when (c) {
    in '0'..'9', in 'a'..'f', in 'A'..'F' -> true
    else -> false
}

fun isBinDigit(c: Char): Boolean = when (c) {
    '0', '1' -> true
    else -> false
}

fun isBinDigitOrSeparator(c: Char): Boolean = when (c) {
    '0', '1', '_' -> true
    else -> false
}

fun isWordChar(c: Char): Boolean = when (c) {
    in 'a'..'z', in 'A'..'Z', in '0'..'9', '_' -> true
    else -> false
}

fun isSymbol(c: Char): Boolean = when (c) {
    in '!'..'/', in ':'..'@', in '['..'`', in '{'..'~' -> true
    else -> false
}

fun isQuoteChar(c: Char): Boolean = when (c) {
    '"', '\'', '`' -> true
    else -> false
}

fun lookahead(input: String, pos: Int, predicate: (Char) -> Boolean): Boolean =
    pos < input.length && predicate(input[pos])

inline fun consume(input: String, start: Int, predicate: (Char) -> Boolean): String {
    var pos = start + 1
    while (pos < input.length && predicate(input[pos])) pos += 1
    return input.substring(start, pos)
}

fun consumeEnclosed(input: String, start: Int, delimiter: Char): String {
    var pos = start + 1
    while (pos < input.length && (input[pos] != delimiter || isEscaped(pos, input))) pos += 1
    val end = if (pos < input.length) pos + 1 else pos
    return input.substring(start, end)
}

private fun isEscaped(pos: Int, input: String) = pos > 0 && input[pos - 1] == '\\'