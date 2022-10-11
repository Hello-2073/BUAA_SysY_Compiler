package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class Exp extends Nonterminal implements Calculable {
    private AddExp addExp;

    public Exp() {
        super(SyntaxType.Exp);
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
