import org.maduralang.compiler.Token
import org.maduralang.compiler.TokenType.*

class Lexer {

    companion object {
        private val KEYWORDS = setOf("fun", "if")
    }

    fun scan(input: String): List<Token> {
        val tokens = ArrayList<Token>()

        var pos = 0
        while (pos < input.length) {
            val c = input[pos]

            // Handle whitespace
            if (c.isWhitespace()) {
                pos++
                continue
            }

            // Handle numbers
            if (c.isDigit()) {
                val number = consumeNumber(input, pos)
                tokens.add(Token(NUMBER, number))
                pos += number.length
                continue
            }

            // Handle keywords
            if (c.isLetter()) {
                val keyword = consumeWord(input, pos)
                tokens.add(Token(KEYWORD, keyword))
                pos += keyword.length
                continue
            }

            // Handle annotations
            if (c == '@') {
                val annotation = consumeAnnotation(input, pos)
                tokens.add(Token(META, annotation))
                pos += annotation.length
                continue
            }

            // Handle comments
            if (c == '/') {
                if (input[pos + 1] == '/') {
                    pos += consumeComment(pos)
                    continue
                } else if (input[pos + 1] == '*') {
                    pos += consumeBlockComment(pos)
                    continue
                }
            }

            // Handle symbols
            if (c in '!'..'~') {
                val symbol = consumeSymbol(input, pos)
                tokens.add(Token(SYMBOL, symbol))
                pos += symbol.length
                continue
            }

            // Handle invalid characters
            tokens.add(Token(INVALID, c.toString()))
            pos++
        }

        return tokens
    }

    private fun consumeNumber(input: String, start: Int): String {
        var pos = start
        while (pos < input.length && input[pos].isDigit()) {
            pos++
        }

        return input.substring(start, pos)
    }

    private fun consumeWord(input: String, start: Int): String {
        var pos = start
        while (pos < input.length && input[pos].isWordChar()) {
            pos++
        }

        return input.substring(start, pos)
    }

    private fun Char.isWordChar(): Boolean =
        when (this) {
            in 'a'..'z',
            in 'A'..'Z',
            in '0'..'9',
            '_' -> true

            else -> false
        }


}

/*
let Lexer = (function () {

	const ALLOWED_KEYWORDS = new Set(['as', 'catch', 'class', 'do', 'else', 'enum', 'false', 'for', 'fun', 'if', 'import', 'in', 'inline', 'is', 'jump', 'let',
		'match', 'mut', 'null', 'out', 'private', 'protected', 'public', 'return', 'shared', 'super', 'this', 'throw', 'true', 'try', 'typealias', 'while']);

	function scan(data, ignoreWhitespaces) {
		let tokens = [];
		let pos = 0;
		let id = 0;

		while (pos < data.length) {
			let c = data.charCodeAt(pos);
			let token =
				(c >= LOWER_A && c <= LOWER_Z) ? consumeNameOrKeyword(data, pos) :
				(c >= UPPER_A && c <= UPPER_Z) ? consumeNameOrType(data, pos) :
				(c >= DIGIT_0 && c <= DIGIT_9) ? consumeNumber(data, pos) :
				(c >= NUL && c <= SPACE || c === NBSP || c === DEL) ? consumeNonPrintable(data, pos) :
				(isSymbol(c)) ? consumeSymbolOrOperator(data, pos) :
					consumeInvalid(data, pos);

			token.id = id++;
			pos += token.data.length;

			if (!ignoreWhitespaces || token.type !== WHITESPACE) {
				tokens.push(token);
			}
		}

		return tokens;
	}

	function consumeNameOrKeyword(data, start) {
		let sequence = match(data, /b'\\?[ -~]'/y, start);
		if (sequence) return new Token(NUMBER, sequence);
		sequence = match(data, /[a-z]\w*/y, start);
let type = ALLOWED_KEYWORDS.has(sequence) ? KEYWORD : NAME;
return new Token(type, sequence);
}

function consumeNameOrType(data , start) {
    return new Token (NAME, match(data, /[A-Z]\w*/y, start));
}

function consumeNumber(data , start) {
    let sequence = matchAll (data, [NUM_PATTERN, HEX_PATTERN, BIN_PATTERN, INT_PATTERN], start);
    return new Token (NUMBER, sequence);
}

function consumeNonPrintable(data , start) {
    let c = data . charCodeAt (start);
    return isWhiteSpace(c) ? new Token(WHITESPACE, consume(data, start, c => isWhiteSpace(c))) :
    new Token ((c === LF) ? LINEBREAK : WHITESPACE, data.substring(start, start+1));
}

function consumeSymbolOrOperator(data , start) {
    let sequence;

    switch(data.charCodeAt(start)) {
        case QUOTE :
        case SINGLE_QUOTE :
        case BACK_TICK :
        sequence = matchAll(
            data,
            [ / "(?:(?:\\"
        )|[^"\r\n])*"?/y, /'(?:(?:\\')|[^'\r\n])*'?/y, /`(?:(?:\\`)|[^`\r\n])*`?/y], start);
        if (sequence) return new Token (STRING, sequence); break;
        case SLASH :
        sequence = matchAll(data, [ / \ / \ / [^\r\n]*/y], start);
        if (sequence) return new Token (COMMENT, sequence); break;
        case DOT :
        sequence = match(data, NUM_PATTERN, start);
        if (sequence) return new Token (NUMBER, sequence); break;
        case AT :
        sequence = match(data, /@( ?: [A-Z]\w*)?/y, start)
        if (sequence) return new Token (META, sequence); break;
    }

    sequence = matchAll(
        data,
        [ / [+\ - * / \^ % < >] = ?/y, /&[& = ]?/y, /\|[\| = ]?/y, /= >/y, /[ != ] = ? = ?/y, /[\?\.]\.?/y, /::?/y], start);
    if (sequence) return new Token (SYMBOL, sequence);

    return new Token (SYMBOL, data.substring(start, start+1));
}

function consumeInvalid(data , start) {
    return new Token (INVALID, consume(data, start, c => c > DEL && c !== NBSP));
}

function matchAll(data , patterns, start) {
    for (let p of patterns) {
        let sequence = match (data, p, start);
        if (sequence) return sequence;
    }

    return null;
}

function match(data , pattern, start) {
    pattern.lastIndex = start;
    let sequence = pattern . exec (data);
    return (sequence) ? sequence[0] : null;
}

function consume(data , start, doesApply) {
    for (var pos = start + 1; pos < data.length && doesApply(data.charCodeAt(pos)); ++pos);
    return data.substring(start, pos);
}

function isSymbol(c) {
    return c >= EXCLAMATION_MARK && c <= SLASH
            || c >= COLON && c <= AT
            || c >= SQUARE_BRACKET && c <= BACK_TICK
            || c >= CURLY_BRACKET && c <= TILDE;
}

function isWhiteSpace(c) {
    return c === SPACE || c === NBSP || c === TAB;
}

return { scan: (data, ignoreWhitespaces) => scan(data, ignoreWhitespaces) };

})();

const EXCLAMATION_MARK = 33;
const QUOTE = 34;
const SINGLE_QUOTE = 39;
const DOT = 46;
const SLASH = 47;
const COLON = 58;
const AT = 64;
const SQUARE_BRACKET = 91;
const BACK_TICK = 96;
const CURLY_BRACKET = 123;
const TILDE = 126;
const DEL = 127;

const LOWER_A = 97;
const LOWER_Z = 122;
const UPPER_A = 65;
const UPPER_Z = 90;
const DIGIT_0 = 48;
const DIGIT_9 = 57;

const NUL = 0;
const SPACE = 32;
const TAB = 9;
const LF = 10;
const NBSP = 160;