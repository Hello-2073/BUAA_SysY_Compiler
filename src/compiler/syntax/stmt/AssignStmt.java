package compiler.syntax.stmt;

import compiler.representation.Generator;
import compiler.representation.quaternion.Save;
import compiler.representation.quaternion.Single;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Syntax;
import compiler.syntax.exp.Exp;
import compiler.syntax.exp.LVal;

import java.util.HashMap;

public class AssignStmt extends Stmt {
    private LVal lVal;
    private Exp exp;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case LVal:
                lVal = (LVal) child;
                break;
            case Exp:
                exp = (Exp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        params.replace("lValUsage", "def");
        lVal.translate(rets, params);
        Arg dst = (Arg) rets.get("dst");
        Arg offset = (Arg) rets.get("offset");
        exp.translate(rets, params);
        Arg src = (Arg) rets.get("dst");
        if (offset != null) {
            Generator.addQuaternion(new Save(src, dst, offset));
        } else {
            Generator.addQuaternion(new Single("+", dst, src));
        }
    }
}
