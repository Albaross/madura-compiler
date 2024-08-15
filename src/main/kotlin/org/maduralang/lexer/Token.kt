package org.maduralang.lexer

interface Token {
    val data: String
    val type: TokenType
}

data class WhitespaceToken(override val data: String) : Token {
    override val type: TokenType get() = TokenType.WHITESPACE
    override fun toString(): String = "$type"
}

data class NameToken(override val data: String) : Token {
    override val type: TokenType get() = TokenType.NAME
    override fun toString(): String = "$data : $type"
}

data class NumberToken(override val data: String) : Token {
    override val type: TokenType get() = TokenType.NUMBER
    override fun toString(): String = "$data : $type"
}

data class SymbolToken(override val data: String) : Token {
    override val type: TokenType get() = TokenType.SYMBOL
    override fun toString(): String = "$data : $type"
}

data class InvalidToken(override val data: String) : Token {
    override val type: TokenType get() = TokenType.INVALID
    override fun toString(): String = "$type"
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