const val WHITESPACE = 0;
const val LINEBREAK = 1;
const val NAME = 2;
const val KEYWORD = 3;
const val NUMBER = 4;
const val STRING = 5;
const val SYMBOL = 6;
const val COMMENT = 7;
const val META = 8;
const val INVALID = 9;

class Token { var type: Int; var data: String
    constructor(type: Int, data: String) {
        this.type = type;
        this.data = data;
    }

    fun print(): String {
        return this.data;
    }
}