package compiler.syntax.exp.unaryExp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.exp.Calculable;
import compiler.syntax.exp.UnaryOp;
import compiler.type.SyntaxType;

public class UnaryExp extends Nonterminal implements Calculable {
    private UnaryOp op;
    private UnaryExp exp;

    public UnaryExp() {
        super(SyntaxType.UnaryExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case UnaryOp:
                op = (UnaryOp) child;
                break;
            case UnaryExp:
                exp = (UnaryExp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
    }

    @Override
    public Integer getDim() {
        return exp.getDim();
    }
}
