package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.Terminal;

import java.util.HashMap;

public class EqExp extends Nonterminal {
    private EqExp eqExp = null;
    private Terminal op = null;
    private RelExp relExp = null;


    public EqExp() {
        super(SyntaxType.EqExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case EqExp:
                eqExp = (EqExp) child;
                break;
            case RelExp:
                relExp = (RelExp) child;
                break;
            case EQL:
            case NEQ:
                op = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        assert relExp != null;
        relExp.translate(rets, params);
        if (eqExp != null) {
            assert op != null;
            Arg src2 = (Arg) rets.get("dst");
            eqExp.translate(rets, params);
            Arg src1 = (Arg) rets.get("dst");
            Arg dst = Generator.addBinary(op.getContent(), src1, src2);
            rets.replace("dst", dst);
        }
    }
}
