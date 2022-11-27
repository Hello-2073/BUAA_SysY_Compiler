package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.representation.quaternion.Binary;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class AddExp extends Nonterminal {
    private AddExp addExp = null;
    private Terminal op = null;
    private MulExp mulExp = null;

    public AddExp() {
        super(SyntaxType.AddExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case AddExp:
                addExp = (AddExp) child;
                break;
            case PLUS:
            case MINU:
                op = (Terminal) child;
                break;
            case MulExp:
                mulExp = (MulExp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        if (addExp != null) {
            addExp.translate(rets, params);
            Arg src1 = (Arg) rets.get("dst");
            mulExp.translate(rets, params);
            Arg src2 = (Arg) rets.get("dst");
            Arg dst = Generator.addBinary(op.getContent(), src1, src2);
            rets.put("dst", dst);
        } else {
            mulExp.translate(rets, params);
        }
    }
}
