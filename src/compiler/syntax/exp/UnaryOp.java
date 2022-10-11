package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

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
    public void translate() {
        super.translate();
    }
}
