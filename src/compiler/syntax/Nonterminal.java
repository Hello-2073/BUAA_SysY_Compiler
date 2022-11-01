package compiler.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static compiler.syntax.SyntaxType.*;

public abstract class Nonterminal extends Syntax {
    protected final List<Syntax> children = new ArrayList<>();

    public Nonterminal(SyntaxType type) {
        super(type);
    }

    public void addChild(Syntax child) {
        children.add(child);
    }

    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        for (Syntax syntax : children) {
            if (syntax instanceof Nonterminal) {
                ((Nonterminal) syntax).translate(rets, params);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Syntax child : children) {
            sb.append(child.toString());
            SyntaxType type = getType();
            if (type != BlockItem
                    && type != Decl
                    && type != BType) {
                sb.append('\n');
            }
        }
        SyntaxType type = getType();
        if (type != BlockItem
            && type != Decl
            && type != BType) {
            sb.append("<");
            sb.append(type);
            sb.append(">");
        }
        return sb.toString();
    }
}
