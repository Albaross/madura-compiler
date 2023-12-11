interface Node

data class Module(val defs: List<Node>) : Node {

    override fun toString(): String {
        return "{\"defs\": $defs}"
    }
}

data class Function(
    val name: Token,
    val params: List<Node>,
    val type: Token = Token(NAME, "None"),
    val body: List<Node>) :
    Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"params\": $params, \"type\": \"$type\", \"body\": $body}"
    }
}

data class Parameter(val name: Token, val type: Token) : Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"type\": \"$type\"}"
    }
}

data class Call(val name: Token, val args: List<Node>) : Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"args\": $args}"
    }
}