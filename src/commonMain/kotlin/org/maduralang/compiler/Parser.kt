package org.maduralang.compiler

class Parser(private val tokens: Iterator<Token>) {
    
    private lateinit var look: Token

    fun parse(): Node {
        return readModule()
    }

    private fun readModule(): Module {
        val defs = ArrayList<Node>()

        while (tokens.hasNext()) {
            move()
            when (look.type) {
                KEYWORD -> defs.add(readDefinition())
                LINEBREAK, COMMENT, META -> break
                else -> throw Error("syntax error", look)
            }
        }

        return Module(defs)
    }

    private fun readDefinition(): Node {
        when (look.data) {
            "fun" -> return readFunction()
            "var",
            "class",
            "public",
            "private",
            "protected" -> throw Error("not yet implemented", look)

            else -> throw Error("syntax error", look)
        }
    }

    private fun readFunction(): Function {
        move()
        val name = matchType(NAME)
        move()
        match("(")
        val params = collect(::readParameter, ")", ",")
        move()
        var type: Token? = null

        if (look.data == ":") {
            type = readType()
            move()
        }

        val body = when (look.data) {
            "{" -> collect(::readStatement, "}", "\n")
            "=>" -> listOf(readStatement())
            else -> throw Error("syntax error", look)
        }

        return Function(name, params, type, body)
    }

    private fun readParameter(): Parameter {
        val name = matchType(NAME)
        move()
        match(":")
        val type = readType()
        return Parameter(name, type)
    }

    private fun readType(): Token {
        move()
        return matchType(NAME)
    }

    private fun readStatement(): Node {
        move()
        if (look.type == NAME) {
            return readCall()
        }
        throw Error("syntax error", look)
    }

    private fun readExpression(): Node {
        return when (look.type) {
            NUMBER, STRING -> Constant(look)
            NAME -> readCall()
            else -> throw Error("syntax error", look)
        }
    }

    private fun readCall(): Call {
        val name = look
        move()
        match("(")
        val args = collect(::readExpression, ")", ",")
        return Call(name, args)
    }

    private fun collect(
        read: () -> Node,
        delimiter: String,
        separator: String? = null
    ): List<Node> {
        val list = ArrayList<Node>()
        var counter = 0

        move()
        while (look.data != delimiter) {
            if (separator != null && counter > 0) {
                match(separator)
                move()
            }
            list.add(read())
            move()
            counter++
        }

        return list
    }

    private fun match(data: String): Token {
        if (look.data != data) throw Error("syntax error", look)
        return look
    }

    private fun matchType(type: Int): Token {
        if (look.type != type) throw Error("syntax error", look)
        return look
    }

    private fun move() {
        look = tokens.next()
    }

    class Error(message: String, val token: Token? = null) : Throwable(message)

}