package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class UnaryOp extends Nonterminal {
    private Terminal op;

    public UnaryOp() {
        super(SyntaxType.UnaryOp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case MINU:
            case PLUS:
            case NOT:
                op = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.put("op", op.getContent());
    }
}
