const WHITESPACE = 0;
const LINEBREAK = 1;
const NAME = 2;
const KEYWORD = 3;
const NUMBER = 4;
const STRING = 5;
const SYMBOL = 6;
const COMMENT = 7;
const META = 8;
const INVALID = 9;

class Token {
    constructor(type, data) {
        this.type = type;
        this.data = data;
    }

    print() {
        return this.data;
    }
}