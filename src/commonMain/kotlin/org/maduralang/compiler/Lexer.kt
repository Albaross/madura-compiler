import org.maduralang.compiler.Token
import org.maduralang.compiler.TokenType.*

class Lexer(
    keywords: List<String> = emptyList(),
    private val ignoreWhitespaces: Boolean = true
) {

    private val keywords: Set<String> = HashSet(keywords)

    fun scan(data: String): List<Token> {
        val tokens = ArrayList<Token>()
        var pos = 0

        while (pos < data.length) {
            val c = data[pos]
            val token = when {
                isWhiteSpace(c) -> handleWhitespace(data, pos)
                isDigit(c) -> handleChar(data, pos)
                isLetter(c) -> handleWord(data, pos)
                c in '!'..'~' -> handleSymbol(data, pos)
                else -> handleInvalid(data, pos)
            }

            if (!ignoreWhitespaces || token.type != WHITESPACE) {
                tokens.add(token)
            }

            pos += token.data.length
        }

        return tokens
    }

    fun handleWhitespace(data: String, start: Int): Token {
        val value = consume(data, start) { isWhiteSpace(it) }
        return Token(type = WHITESPACE, data = value)
    }

    fun handleWord(data: String, start: Int): Token {
        val value = consume(data, start) { isWordChar(it) }
        return Token(type = if (value in keywords) KEYWORD else NAME, data = value)
    }

    fun handleChar(data: String, start: Int): Token {
        var pos = start
        var value = consume(data, pos) { isDigit(it) || it == '_' }
        pos += value.length
        if (data.length > (pos + 1) && data[pos] == '.' && isDigit(data[pos + 1])) {
            value += consume(data, pos) { isDigit(it) || it == '_' }
        }
        return Token(type = NUMBER, data = value)
    }

    fun handleSymbol(data: String, start: Int): Token =
        Token(type = SYMBOL, data = data.substring(start, start + 1))

    fun handleInvalid(data: String, start: Int) =
        Token(type = INVALID, data = data.substring(start, start + 1))

    fun consume(data: String, start: Int, predicate: (Char) -> Boolean): String {
        var pos = start + 1
        while (pos < data.length && predicate(data[pos])) {
            pos++
        }
        return data.substring(start, pos)
    }

    fun isWhiteSpace(c: Char): Boolean = when (c) {
        ' ', '\t', '\n', '\r', '\u00A0', '\u000B', '\u000C', '\u0085' -> true
        else -> false
    }

    fun isDigit(c: Char): Boolean = c in '0'..'9'

    fun isLetter(c: Char): Boolean = when (c) {
        in 'a'..'z', in 'A'..'Z' -> true
        else -> false
    }

    fun isWordChar(c: Char): Boolean = when (c) {
        in 'a'..'z', in 'A'..'Z', in '0'..'9', '_' -> true
        else -> false
    }

    fun startsWithUppercase(data: String): Boolean = data[0] in 'A'..'Z'

}