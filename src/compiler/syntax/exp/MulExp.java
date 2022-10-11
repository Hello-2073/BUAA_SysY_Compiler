package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.unaryExp.UnaryExp;
import compiler.type.SyntaxType;

public class MulExp extends Nonterminal implements Calculable {
    private MulExp mulExp = null;
    private Terminal op = null;
    private UnaryExp unaryExp = null;

    public MulExp() {
        super(SyntaxType.MulExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case MulExp:
                mulExp = (MulExp) child;
                break;
            case MULT:
            case DIV:
            case MOD:
                op = (Terminal) child;
                break;
            case UnaryExp:
                unaryExp = (UnaryExp) child;
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
        return unaryExp.getDim();
    }
}
