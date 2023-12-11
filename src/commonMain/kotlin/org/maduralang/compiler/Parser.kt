package org.maduralang.compiler

class Parser() {

    fun parse(tokenList: List<Token>): Node {
        return readModule(Tokens(tokenList));
    }

    fun readModule(tokens: Tokens): Module {
        val defs = ArrayList<Node>();

        while (tokens.hasNext()) {
            var t = tokens.next();
            when (t.type) {
                KEYWORD -> defs.add(readDefinition(t, tokens));
                LINEBREAK,
                COMMENT,
                META -> break;
                else -> throw Error("syntax error", t);
            }
        }

        return Module(defs);
    }

    fun readDefinition(t: Token, tokens: Tokens): Node {
        when (t.data) {
            "fun" -> return readFunction(t, tokens);
            "var",
            "class",
            "public",
            "private",
            "protected" -> throw Error("not yet implemented", t);
            else -> throw Error("syntax arror", t);
        }
    }

    fun readFunction(token: Token, tokens: Tokens): Function {
        var name = matchType(tokens.next(), NAME);
        match(tokens.next(), "(");
        var params = collect(tokens, ::readParameter, ")", ",");
        var t = tokens.next();
        var type: Token? = null;

        if (t.data === ":") {
            type = readType(tokens);
            t = tokens.next();
        }

        var body = emptyList<Node>();
        when (t.data) {
            "{" -> body = collect(tokens, ::readStatement, "}", "\n");
            "=>" -> body = listOf(readStatement(tokens.next(), tokens));
        }

        return Function(name, params, type, body);
    }

    fun readParameter(t: Token, tokens: Tokens): Parameter {
        var name = matchType(t, NAME);
        match(tokens.next(), ":");
        var type = readType(tokens);
        return Parameter(name, type);
    }

    fun readType(tokens: Tokens): Token {
        return matchType(tokens.next(), NAME);
    }

    fun readStatement(t: Token, tokens: Tokens): Node {
        if (t.type === NAME) {
            return readCall(t, tokens);
        }
        throw Error("syntax error", t);
    }

    fun readExpression(t: Token, tokens: Tokens): Node {
        when (t.type) {
            NUMBER,
            STRING -> return Constant(t);
            NAME -> return readCall(t, tokens);
        }
        throw Error("systax error", t);
    }

    fun readCall(t: Token, tokens: Tokens): Call {
        var name = t;
        match(tokens.next(), "(");
        var args = collect(tokens, ::readExpression, ")", ",");
        return Call(name, args);
    }

    fun collect(
        tokens: Tokens,
        read: (Token, Tokens) -> Node,
        delimiter: String,
        separator: String? = null
    ): List<Node> {
        val list = ArrayList<Node>();
        var counter = 0;

        while (tokens.hasNext()) {
            var t = tokens.next();
            if (t.data === delimiter) return list;
            if (separator != null && counter > 0) {
                match(t, separator);
                t = tokens.next();
            }
            list.add(read(t, tokens));
            counter++;
        }

        throw Error("end of file");
    }

    fun match(token: Token, data: String): Token {
        if (token.data !== data) throw Error("syntax error", token);
        return token;
    }

    fun matchType(token: Token, type: Int): Token {
        if (token.type !== type) throw Error("syntax error", token);
        return token;
    }

    class Tokens {
        var tokenList: List<Token>;
        var index: Int

        constructor(tokenList: List<Token>) {
            this.tokenList = tokenList;
            this.index = 0;
        }

        fun hasNext(): Boolean {
            return (this.index < this.tokenList.size);
        }

        fun next(): Token {
            if (!this.hasNext()) throw Error("end of file");
            return this.tokenList[this.index++];
        }

    }

    class Error : Throwable {
        var token: Token?

        constructor(message: String, token: Token? = null) : super(message) {
            this.token = token;
        }

    }

}