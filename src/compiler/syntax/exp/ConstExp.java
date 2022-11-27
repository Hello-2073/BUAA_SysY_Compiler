package compiler.syntax.exp;

import compiler.representation.quaternion.opnum.Imm;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class ConstExp extends Nonterminal {
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
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        addExp.translate(rets, params);
        assert rets.get("dst") instanceof Imm;
    }
}
