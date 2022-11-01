package compiler.lexer;

import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

public class Lexer {
    private final Source source;

    public Lexer(Source source) {
        this.source = source;
    }

    public boolean hasNext() {
        skipEmptyAndComment();
        return !source.isEnd();
    }

    public Terminal next() {
        source.flush();
        SyntaxType nextType = getNextType();
        if (nextType == SyntaxType.EOF) {
            return null;
        }
        return new Terminal(nextType, source.readBuffer(), source.getRow());
    }

    private SyntaxType getNextType() {
        skipEmptyAndComment();

        if (source.isEnd())
            return SyntaxType.EOF;

        if (source.isLetter()) {
            return ident();
        } else if (source.isDigit()) {
            while (source.isDigit())
                source.getChar();
            return SyntaxType.INTCON;
        } else {
            switch (source.getChar()) {
                case '&':
                    if (source.tryGetChar('&')) {
                        return SyntaxType.AND;
                    } else {
                        return SyntaxType.ERROR;
                    }
                case '|':
                    if (source.tryGetChar('|')) {
                        return SyntaxType.OR;
                    } else {
                        return SyntaxType.ERROR;
                    }
                case '>':
                    if (source.tryGetChar('=')) {
                        return SyntaxType.GEQ;
                    } else {
                        return SyntaxType.GRE;
                    }
                case '<':
                    if (source.tryGetChar('=')) {
                        return SyntaxType.LEQ;
                    } else {
                        return SyntaxType.LSS;
                    }
                case '!':
                    if (source.tryGetChar('=')) {
                        return SyntaxType.NEQ;
                    } else {
                        return SyntaxType.NOT;
                    }
                case '=':
                    if (source.tryGetChar('=')) {
                        return SyntaxType.EQL;
                    } else {
                        return SyntaxType.ASSIGN;
                    }
                case '"':
                    return formatString();
                case '/':
                    return SyntaxType.DIV;
                case '*':
                    return SyntaxType.MULT;
                case '+':
                    return SyntaxType.PLUS;
                case '-':
                    return SyntaxType.MINU;
                case '%':
                    return SyntaxType.MOD;
                case ';':
                    return SyntaxType.SEMICN;
                case ',':
                    return SyntaxType.COMMA;
                case '(':
                    return SyntaxType.LPARENT;
                case ')':
                    return SyntaxType.RPARENT;
                case '[':
                    return SyntaxType.LBRACK;
                case ']':
                    return SyntaxType.RBRACK;
                case '{':
                    return SyntaxType.LBRACE;
                case '}':
                    return SyntaxType.RBRACE;
                default:
                    return SyntaxType.ERROR;
            }
        }
    }

    private SyntaxType ident() {
        while (source.isLetter() || source.isDigit())
            source.getChar();
        switch (source.readBuffer()) {
            case "main":
                return SyntaxType.MAINTK;
            case "const":
                return SyntaxType.CONSTTK;
            case "int":
                return SyntaxType.INTTK;
            case "break":
                return SyntaxType.BREAKTK;
            case "continue":
                return SyntaxType.CONTINUETK;
            case "if":
                return SyntaxType.IFTK;
            case "else":
                return SyntaxType.ELSETK;
            case "while":
                return SyntaxType.WHILETK;
            case "getint":
                return SyntaxType.GETINTTK;
            case "printf":
                return SyntaxType.PRINTFTK;
            case "return":
                return SyntaxType.RETURNTK;
            case "void":
                return SyntaxType.VOIDTK;
            default:
                return SyntaxType.IDENFR;
        }
    }

    private SyntaxType formatString() {
        while (true) {
            char ch = source.getChar();
            if (ch == '"') {
                break;
            }
        }
        return SyntaxType.STRCON;
    }

    private void skipEmptyAndComment() {
        while (!source.isEnd()) {
            if (source.isEmpty()) {
                source.getChar();
            } else if (source.check(0) != '/') {
                break;
            } else {
                char ch = source.check(1);
                if (ch == '*') {
                    source.getChar();
                    source.getChar();
                    skipBlockComment();
                } else if (ch == '/') {
                    source.getChar();
                    source.getChar();
                    skipInlineComment();
                } else {
                    break;
                }
            }
        }
        source.flush();
    }

    private void skipInlineComment() {
        while (!source.isEnd()) {
            if (source.getChar() == '\n') {
                break;
            }
        }
        source.flush();
    }

    private void skipBlockComment() {
        while (!source.isEnd()) {
            if (source.getChar() != '*') continue;
            if (!source.isEnd() && source.tryGetChar('/')) {
                break;
            }
        }
        source.flush();
    }
}
