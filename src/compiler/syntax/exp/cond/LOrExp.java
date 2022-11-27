package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class LOrExp extends Nonterminal {
    private LOrExp lOrExp = null;
    private LAndExp lAndExp = null;

    public LOrExp() {
        super(SyntaxType.LOrExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case LOrExp:
                lOrExp = (LOrExp) child;
                break;
            case LAndExp:
                lAndExp = (LAndExp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        assert lAndExp != null;
        if (lOrExp != null) {
            lOrExp.translate(rets, params);
        }
        Label trueForLOrExp = (Label) params.get("trueForLOrExp");
        Label falseForLAndExp = Generator.allocLabel();
        params.put("falseForLAndExp", falseForLAndExp);
        lAndExp.translate(rets, params);
        Generator.addQuaternion(new Jump(trueForLOrExp));
        Generator.addLabel(falseForLAndExp);
    }
}
