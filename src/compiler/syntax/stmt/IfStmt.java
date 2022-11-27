package compiler.syntax.stmt;

import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Syntax;
import compiler.syntax.exp.cond.Cond;

import java.util.HashMap;

import static compiler.symbol.scope.ScopeType.IF;

public class IfStmt extends Stmt {
    private Cond cond = null;
    private Stmt stmt = null;
    private Stmt elseStmt = null;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Cond:
                cond = (Cond) child;
                break;
            case Stmt:
                if (stmt == null) {
                    stmt = (Stmt) child;
                } else {
                    elseStmt = (Stmt) child;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        Label trueForCond = Generator.allocLabel();
        Label falseForCond = Generator.allocLabel();
        params.put("falseForCond", falseForCond);
        cond.translate(rets, params);
        stmt.translate(rets, params);
        if (elseStmt != null) {
            Label elseEnd = Generator.allocLabel();
            Generator.addQuaternion(new Jump(elseEnd));
            Generator.addLabel(falseForCond);
            elseStmt.translate(rets, params);
            Generator.addLabel(elseEnd);
        } else {
            Generator.addLabel(falseForCond);
        }
        rets.replace("stmtType", "ifStmt");
    }
}
