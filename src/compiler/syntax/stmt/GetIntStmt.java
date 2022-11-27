package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Read;
import compiler.representation.quaternion.Save;
import compiler.representation.quaternion.Single;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Tmp;
import compiler.syntax.Syntax;
import compiler.syntax.exp.LVal;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class GetIntStmt extends Stmt {
    private LVal lVal;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.LVal) {
            lVal = (LVal) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        params.replace("lValUsage", "def");
        lVal.translate(rets, params);
        Arg dst = (Arg) rets.get("dst");
        Arg src1 = (Arg) rets.get("offset");
        if (src1 == null) {
            Generator.addQuaternion(new Read(dst));
            rets.put("dst", dst);
        } else {
            Tmp tmp = Generator.newTmp();
            Generator.addQuaternion(new Read(tmp));
            Generator.addQuaternion(new Save(dst, src1, tmp));
            rets.put("dst", tmp);
        }
        rets.put("offset", null);
        rets.replace("stmtType", "getIntStmt");
    }
}
