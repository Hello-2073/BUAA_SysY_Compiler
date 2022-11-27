package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class Cond extends Nonterminal {
    private LOrExp lOrExp = null;

    public Cond() {
        super(SyntaxType.Cond);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.LOrExp) {
            lOrExp = (LOrExp) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        assert lOrExp != null;
        Label falseForCond = (Label) params.get("falseForCond");
        Label trueForLOrExp = Generator.allocLabel();
        params.put("trueForLOrExp", trueForLOrExp);
        lOrExp.translate(rets, params);
        Generator.addQuaternion(new Jump(falseForCond));
        Generator.addLabel(trueForLOrExp);
    }
}
