package compiler.syntax.exp.cond;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.Terminal;
import compiler.syntax.exp.AddExp;

import java.util.HashMap;

public class RelExp extends Nonterminal {
    private RelExp relExp = null;
    private Terminal op = null;
    private AddExp addExp = null;

    public RelExp() {
        super(SyntaxType.RelExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case RelExp:
                relExp = (RelExp) child;
                break;
            case AddExp:
                addExp = (AddExp) child;
                break;
            case GEQ:
            case LEQ:
            case GRE:
            case LSS:
                op = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        assert addExp != null;
        if (relExp != null) {
            assert op != null;
            relExp.translate(rets, params);
            Arg src1 = (Arg) rets.get("dst");
            addExp.translate(rets, params);
            Arg src2 = (Arg) rets.get("dst");
            Arg dst = Generator.addBinary(op.getContent(), src1, src2);
            rets.replace("dst", dst);
        } else {
            addExp.translate(rets, params);
        }
    }
}
