package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Read;
import compiler.representation.quaternion.opnum.Arg;
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
        if (rets.get("offset") == null) {
            Generator.addQuaternion(new Read(dst));
        } else {
            /* TODO: 数组读取 */
        }
        rets.put("offset", null);
        rets.replace("stmtType", "getIntStmt");
    }
}
