package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class PrimaryExp extends Nonterminal implements Calculable {
    private Calculable child;

    public PrimaryExp() {
        super(SyntaxType.PrimaryExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Exp:
            case LVal:
            case Number:
                this.child = (Calculable) child;
                break;
            default:
                break;
        }
    }

    @Override
    public Integer getDim() {
        return child.getDim();
    }
}
