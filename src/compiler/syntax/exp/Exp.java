package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class Exp extends Nonterminal {
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
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        addExp.translate(rets, params);
    }
}
