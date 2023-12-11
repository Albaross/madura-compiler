package org.maduralang.compiler

import kotlin.test.Test
import kotlin.test.assertEquals

class ParserTest {

    @Test
    fun `input is parsed correctly`() {
        assertEquals(PARSER_OUTPUT, Parser(LEXER_OUTPUT.iterator()).parse())
    }
}

private val LEXER_OUTPUT = listOf(
    Token(KEYWORD, "fun"),
    Token(NAME, "main"),
    Token(SYMBOL, "("),
    Token(SYMBOL, ")"),
    Token(SYMBOL, "=>"),
    Token(NAME, "print"),
    Token(SYMBOL, "("),
    Token(STRING, "\"Hello World\""),
    Token(SYMBOL, ")")
)

private val PARSER_OUTPUT = Module(
    defs = listOf(
        Function(
            name = Token(NAME, "main"),
            body = listOf(
                Call(
                    name = Token(NAME, "print"),
                    args = listOf(Constant(Token(STRING, "\"Hello World\"")))
                )
            )
        )
    )
)