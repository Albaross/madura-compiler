package org.maduralang.lexer

class Lexer {

    fun scan(input: String, ignoreWhitespaces: Boolean = true): List<Token> {
        val tokens = ArrayList<Token>()
        var pos = 0

        while (pos < input.length) {
            val token = createToken(input, pos)
            pos += token.data.length

            if (!ignoreWhitespaces || token !is WhitespaceToken)
                tokens.add(token)
        }

        return tokens
    }

    private fun createToken(input: String, pos: Int): Token {
        val c = input[pos]

        if (isWhitespace(c))
            return WhitespaceToken("$c")

        if (isLetter(c))
            return consumeNameOrKeyword(input, pos)

        if (isDigit(c))
            return consumeNumber(input, pos)

        if (isSymbol(c)) {
            return consumeSymbol(input, pos)
        }

        return InvalidToken("$c")
    }

    private fun consumeNameOrKeyword(input: String, pos: Int): Token {
        val lexeme = consume(input, pos, ::isWordChar)
        return NameToken(lexeme)
    }

    private fun consumeNumber(input: String, pos: Int): NumberToken {
        val integerPart = consume(input, pos, ::isDigit)
        val intermediatePos = pos + integerPart.length

        if (lookahead(input, intermediatePos) { it == '.' }
            && lookahead(input, intermediatePos + 1, ::isDigit)) {

            val fractionPart = consume(input, intermediatePos, ::isDigit)
            return NumberToken(integerPart + fractionPart)
        }

        return NumberToken(integerPart)
    }

    private fun consumeSymbol(input: String, pos: Int): SymbolToken {
        val c = input[pos]
        return SymbolToken("$c")
    }
}