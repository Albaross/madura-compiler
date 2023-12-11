open class Node { var tag: String
    constructor(tag: String = "") {
        this.tag = tag;
    }

    open fun print(): String {
        return this.tag;
    }
}

class Module : Node { var defs: List<Node>
    constructor(defs: List<Node>) : super("module") {
        this.defs = defs;
    }

    override fun print(): String {
        return "{\"defs\": ${printList(this.defs)}}"
    }
}

class Function : Node { var name: Node; var params: List<Node>; var type: Node?; var body: List<Node>
    constructor(name: Node, params: List<Node>, type: Node?, body: List<Node>) : super("fun") {
        this.name = name;
        this.params = params;
        this.type = type;
        this.body = body;
    }

    override fun print(): String {
        return "{\"name\": \"${this.name.print()}\", \"params\": ${printList(this.params)}, \"type\": \"${if (this.type != null) this.type!!.print() else "None"}\", \"body\": ${
            printList(
                this.body
            )
        }}"
    }
}

class Parameter : Node { var name: Node; var type: Node
    constructor(name: Node, type: Node) : super() {
        this.name = name;
        this.type = type;
    }

    override fun print(): String {
        return "{\"name\": \"${this.name.print()}\", \"type\": \"${this.type.print()}\"}"
    }
}

class Call : Node { var name: Node; var args: List<Node>
    constructor(name: Node, args: List<Node>) : super() {
        this.name = name;
        this.args = args;
    }

    override fun print(): String {
        return "{\"name\": \"${this.name.print()}\", \"args\": ${printList(this.args)}}"
    }
}

fun printList(list: List<Node>): String {
    var builder = StringBuilder();
    builder.append("[");

    for (i in 0..<list.size) {
        if (i > 0) builder.append(", ");
        builder.append(list[i].print());
    }

    builder.append("]");
    return builder.toString();
}