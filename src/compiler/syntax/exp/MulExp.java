package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.representation.quaternion.Binary;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.unaryExp.UnaryExp;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class MulExp extends Nonterminal {
    private MulExp mulExp = null;
    private Terminal op = null;
    private UnaryExp unaryExp = null;

    public MulExp() {
        super(SyntaxType.MulExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case MulExp:
                mulExp = (MulExp) child;
                break;
            case MULT:
            case DIV:
            case MOD:
                op = (Terminal) child;
                break;
            case UnaryExp:
                unaryExp = (UnaryExp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        unaryExp.translate(rets, params);
        if (mulExp != null) {
            assert op != null;
            Arg src2 = (Arg) rets.get("dst");
            mulExp.translate(rets, params);
            Arg src1 = (Arg) rets.get("dst");
            Arg dst = Generator.addBinary(op.getContent(), src1, src2);
            rets.put("dst", dst);
        }
    }
}
