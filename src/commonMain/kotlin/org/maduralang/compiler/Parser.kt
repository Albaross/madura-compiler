package org.maduralang.compiler

import org.maduralang.compiler.tokens.Token
import org.maduralang.compiler.tokens.TokenType
import org.maduralang.compiler.tokens.TokenType.*

class Parser(private val tokens: Iterator<Token>) {

    private lateinit var look: Token

    fun parse(): Node {
        return readModule()
    }

    private fun readModule(): Module {
        val defs = ArrayList<Definition>()

        while (tokens.hasNext()) {
            move()
            when (look.type) {
                KEYWORD -> defs.add(readDefinition())
                COMMENT, META -> break
                else -> throw Error("syntax error", look)
            }
        }

        return Module(defs)
    }

    private fun readDefinition(): Definition {
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
            "{" -> collect(::readStatement, "}")
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

    private fun readStatement(): Statement {
        move()
        if (look.type == NAME) {
            return readCall()
        }
        throw Error("syntax error", look)
    }

    private fun readExpression(): Expression {
        return when (look.type) {
            NUMBER, TEXT -> Constant(look)
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

    private fun <T: Node> collect(
        read: () -> T,
        delimiter: String,
        separator: String? = null
    ): List<T> {
        val list = ArrayList<T>()
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

    private fun matchType(type: TokenType): Token {
        if (look.type != type) throw Error("syntax error", look)
        return look
    }

    private fun move() {
        look = tokens.next()
    }

    class Error(message: String, val token: Token? = null) : Throwable(message)

}