package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class LAndExp extends Nonterminal {
    private EqExp eqExp = null;
    private LAndExp lAndExp = null;

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
        eqExp.translate(rets, params);
        if (lAndExp != null) {
            Arg src2 = (Arg) rets.get("dst");
            lAndExp.translate(rets, params);
            Arg src1 = (Arg) rets.get("dst");
            Arg dst = Generator.addBinary("&&", src1, src2);
            rets.replace("dst", dst);
        }

    }
}
