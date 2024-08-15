package org.maduralang.lexer

interface Token {
    val data: String
    val type: TokenType
}

enum class TokenType {
    WHITESPACE,
    NAME,
    KEYWORD,
    NUMBER,
    STRING,
    SYMBOL,
    COMMENT,
    ANNOTATION,
    INVALID
}