package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class ConstExp extends Nonterminal implements Calculable {
    private AddExp addExp;

    public ConstExp() {
        super(SyntaxType.ConstExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.AddExp) {
            addExp = (AddExp) child;
        }
    }

    @Override
    public Integer getDim() {
        return addExp.getDim();
    }
}
