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
            if (c == '.' && lookahead(input, pos + 1, ::isDigit))
                return NumberToken(consume(input, pos, ::isDigitOrSeparator))

            if (isQuoteChar(c))
                return StringToken(consumeEnclosed(input, pos, delimiter = c))

            if (c == '/' && lookahead(input, pos + 1) { it == '/' })
                return CommentToken(consume(input, pos) { it != '\n' && it != '\r' })

            if (c == '@')
                return AnnotationToken(consume(input, pos, ::isWordChar))

            return consumeSymbol(input, pos)
        }

        return InvalidToken("$c")
    }

    private fun consumeNameOrKeyword(input: String, pos: Int): WordToken {
        val lexeme = consume(input, pos, ::isWordChar)
        return if (isKeyword(lexeme)) KeywordToken.valueOf(lexeme.uppercase()) else NameToken(lexeme)
    }

    private fun consumeNumber(input: String, pos: Int): NumberToken {
        if (input[pos] == '0') {
            if (lookahead(input, pos + 1) { it == 'x' } && lookahead(input, pos + 2, ::isHexDigit))
                return NumberToken("0x" + consume(input, pos + 2, ::isHexDigitOrSeparator))

            if (lookahead(input, pos + 1) { it == 'b' } && lookahead(input, pos + 2, ::isBinDigit))
                return NumberToken("0b" + consume(input, pos + 2, ::isBinDigitOrSeparator))
        }

        val integerPart = consume(input, pos, ::isDigitOrSeparator)
        val intermediatePos = pos + integerPart.length

        if (lookahead(input, intermediatePos) { it == '.' }
            && lookahead(input, intermediatePos + 1, ::isDigit)) {

            val fractionPart = consume(input, intermediatePos, ::isDigitOrSeparator)
            return NumberToken(integerPart + fractionPart)
        }

        return NumberToken(integerPart)
    }

    private fun consumeSymbol(input: String, pos: Int): SymbolToken {
        val lexeme = when (val c = input[pos]) {
            '.', '?' -> if (lookahead(input, pos + 1) { it == '.' }) "$c." else "$c"

            '+', '*', '/', '^', '%' ->
                if (lookahead(input, pos + 1) { it == '=' }) "$c=" else "$c"

            '<', '>' -> when {
                lookahead(input, pos + 1) { it == '=' } -> "$c="
                lookahead(input, pos + 1) { it == c } -> if (lookahead(input, pos + 2) { it == c }) "$c$c$c" else "$c$c"
                else -> "$c"
            }

            '-' -> when {
                lookahead(input, pos + 1) { it == '=' } -> "-="
                lookahead(input, pos + 1) { it == '>' } -> "->"
                else -> "-"
            }

            '=' -> when {
                lookahead(input, pos + 1) { it == '=' } -> if (lookahead(input, pos + 2) { it == '=' }) "===" else "=="
                lookahead(input, pos + 1) { it == '>' } -> "=>"
                else -> "="
            }

            '&', '|' -> when {
                lookahead(input, pos + 1) { it == c } -> "$c$c"
                lookahead(input, pos + 1) { it == '=' } -> "$c="
                else -> "$c"
            }


            ':' -> if (lookahead(input, pos + 1) { it == ':' }) "::" else ":"

            else -> "$c"
        }

        return SymbolToken(lexeme)
    }
}