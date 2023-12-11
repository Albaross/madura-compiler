interface Node

class Module(var defs: List<Node>) : Node {

    override fun toString(): String {
        return "{\"defs\": $defs}"
    }
}

class Function(var name: Node, var params: List<Node>, var type: Node?, var body: List<Node>) : Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"params\": $params, \"type\": \"${if (type != null) type else "None"}\", \"body\": $body}"
    }
}

class Parameter(var name: Node, var type: Node) : Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"type\": \"$type\"}"
    }
}

class Call(val name: Node, val args: List<Node>) : Node {

    override fun toString(): String {
        return "{\"name\": \"$name\", \"args\": $args}"
    }
}