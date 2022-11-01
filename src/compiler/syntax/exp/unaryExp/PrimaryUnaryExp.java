package compiler.syntax.exp.unaryExp;

import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.exp.PrimaryExp;

import java.util.HashMap;

public class PrimaryUnaryExp extends UnaryExp {
    private PrimaryExp primaryExp;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.PrimaryExp) {
            primaryExp = (PrimaryExp) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        primaryExp.translate(rets, params);
    }
}
