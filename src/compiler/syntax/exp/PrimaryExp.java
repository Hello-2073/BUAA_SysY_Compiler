package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.representation.quaternion.Load;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class PrimaryExp extends Nonterminal {
    private Exp exp = null;
    private LVal lVal = null;
    private Number number = null;

    public PrimaryExp() {
        super(SyntaxType.PrimaryExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Exp:
                this.exp = (Exp) child;
                break;
            case LVal:
                this.lVal = (LVal) child;
                break;
            case Number:
                this.number = (Number) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        super.translate(rets, params);
        if (lVal != null && rets.get("offset") != null) {
            if ((Integer)rets.get("dim") == 0) {
                Arg src1 = (Arg) rets.get("dst");
                Arg src2 = (Arg) rets.get("offset");
                if (src2 != null) {
                    Arg dst = Generator.newTmp();
                    Generator.addQuaternion(new Load(dst, src1, src2));
                    rets.put("dst", dst);
                }
            } else {
                Arg src1 = (Arg) rets.get("dst");
                Arg src2 = (Arg) rets.get("offset");
                Arg dst = Generator.addBinary("*", src2, new Imm(4));
                dst = Generator.addBinary("+", src1, dst);
                rets.put("dst", dst);
            }
        }
    }
}
