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

fun isWordChar(c: Char): Boolean = when (c) {
    in 'a'..'z', in 'A'..'Z', in '0'..'9', '_' -> true
    else -> false
}

fun isSymbol(c: Char): Boolean = when (c) {
    in '!'..'/', in ':'..'@', in '['..'`', in '{'..'~' -> true
    else -> false
}

fun lookahead(input: String, pos: Int, predicate: (Char) -> Boolean): Boolean =
    pos < input.length && predicate(input[pos])

inline fun consume(input: String, start: Int, predicate: (Char) -> Boolean): String {
    var pos = start + 1
    while (pos < input.length && predicate(input[pos])) pos += 1
    return input.substring(start, pos)
}