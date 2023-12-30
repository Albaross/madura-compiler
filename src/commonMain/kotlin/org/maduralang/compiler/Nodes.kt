package org.maduralang.compiler

import org.maduralang.compiler.tokens.Token

interface Node

interface Statement : Node

interface Expression : Node

interface Definition : Node

data class Module(val defs: List<Definition>) : Node

data class Function(
    val name: Token,
    val params: List<Parameter> = emptyList(),
    val type: Token? = null,
    val body: List<Statement> = emptyList()
) : Definition

data class Parameter(val name: Token, val type: Token) : Node

data class Call(val name: Token, val args: List<Expression>) : Statement, Expression

data class Constant(val token: Token) : Expression

data class Assign(val variable: String, val expr: Expression) : Statement

data class While(val cond: Expression, val body: List<Statement>) : Statement

data class If(val cond: Expression, val thenBody: List<Statement>, val elseBody: List<Statement>) : Statement

data class Variable(val name: String) : Expression

data class BinaryOperation(val op: String, val left: Expression, val right: Expression) : Expression