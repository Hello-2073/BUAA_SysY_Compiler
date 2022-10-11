package compiler;

import compiler.Source;
import compiler.syntax.Terminal;
import compiler.Lexer;
import compiler.type.SyntaxType;

import java.util.ArrayList;
import java.util.List;

public class Tokens {
    private final List<Terminal> tokens;
    private int cur;

    public Tokens(Source source) {
        tokens = new ArrayList<>();
        cur = 0;
        Lexer lexer = new Lexer(source);
        while (lexer.hasNext()) {
            tokens.add(lexer.next());
        }
    }

    public SyntaxType getType(int offset) {
        int n = cur + offset;
        if (n >= tokens.size()) {
            return SyntaxType.EOF;
        }
        return tokens.get(n).getType();
    }

    public boolean hasNext() {
        return cur < tokens.size();
    }

    public Terminal get() {
        return tokens.get(cur++);
    }

    public Terminal tryGet(SyntaxType type) throws Exception {
        SyntaxType realType = tokens.get(cur).getType();
        if (realType != type) {
            throw new Exception();
        }
        return tokens.get(cur++);
    }

    public int getRow(int offset) {
        int index = cur + offset;
        Terminal terminal = tokens.get(index);
        if (terminal == null)
            return -1;
        return terminal.getRow();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Terminal token : tokens) {
            sb.append(token.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
