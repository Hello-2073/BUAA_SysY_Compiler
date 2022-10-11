package compiler.syntax.exp.unaryExp;

import compiler.syntax.Syntax;
import compiler.syntax.exp.Calculable;
import compiler.type.SyntaxType;

public class PrimaryUnaryExp extends UnaryExp {
    private Calculable primaryExp;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.PrimaryExp) {
            primaryExp = (Calculable) child;
        }
    }

    @Override
    public Integer getDim() {
        return primaryExp.getDim();
    }
}
