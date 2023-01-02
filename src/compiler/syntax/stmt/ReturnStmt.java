package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Call;
import compiler.representation.quaternion.Ret;
import compiler.representation.quaternion.opnum.Arg;
import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.Exp;

import java.util.HashMap;

public class ReturnStmt extends Stmt {
    private Terminal returntk;
    private Exp returnValue = null;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Exp:
                returnValue = (Exp) child;
                break;
            case RETURNTK:
                returntk = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.put("stmtType", "returnStmt");
        if ("int".equals(Generator.getCurrentFuncType())) {
            if (returnValue == null) {
                System.out.println("第 " + returntk.getRow() + " 行: 缺少返回值。");
                ErrorRecorder.insert(new Error(returntk.getRow(), "g"));
                Generator.addQuaternion(new Ret(null));
            } else {
                returnValue.translate(rets, params);
                Arg dst = (Arg) rets.get("dst");
                Generator.addQuaternion(new Ret(dst));
            }
        } else {
            if (returnValue != null) {
                System.out.println("第 " + returntk.getRow() + "行: 多余返回值。");
                ErrorRecorder.insert(new Error(returntk.getRow(), "f"));
            }
            Generator.addQuaternion(new Ret(null));
        }
    }
}
