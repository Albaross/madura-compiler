package org.maduralang.compiler

enum class TokenType {
    WHITESPACE, NAME, KEYWORD, NUMBER, STRING, SYMBOL, COMMENT, META, INVALID
}

data class Token(val type: TokenType, val data: String) {

    override fun toString(): String = data
}