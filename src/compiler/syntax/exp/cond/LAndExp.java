package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.Break;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class LAndExp extends Nonterminal {
    private LAndExp lAndExp = null;
    private EqExp eqExp = null;

    public LAndExp() {
        super(SyntaxType.LAndExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case EqExp:
                eqExp = (EqExp) child;
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
        assert eqExp != null;
        if (lAndExp != null) {
            lAndExp.translate(rets, params);
        }
        Label falseForLAndExp = (Label) params.get("falseForLAndExp");
        eqExp.translate(rets, params);
        Arg dst = (Arg) rets.get("dst");
        Generator.addBreakIf("==", dst, new Imm(0), falseForLAndExp);
    }
}
