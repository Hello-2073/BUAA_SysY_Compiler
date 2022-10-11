package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

public class AddExp extends Nonterminal implements Calculable {
    private AddExp addExp = null;
    private Terminal op = null;
    private MulExp mulExp = null;

    public AddExp() {
        super(SyntaxType.AddExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case AddExp:
                addExp = (AddExp) child;
                break;
            case PLUS:
            case MINU:
                op = (Terminal) child;
                break;
            case MulExp:
                mulExp = (MulExp) child;
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
        return mulExp.getDim();
    }
}
