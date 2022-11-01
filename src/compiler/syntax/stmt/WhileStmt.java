package compiler.syntax.stmt;

import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Syntax;
import compiler.syntax.exp.cond.Cond;

import java.util.HashMap;

import static compiler.symbol.scope.ScopeType.WHILE;

public class WhileStmt extends Stmt {
    private Cond cond = null;
    private Stmt stmt = null;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Cond:
                cond = (Cond) child;
                break;
            case Stmt:
                stmt = (Stmt) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        Label label = Generator.addLabel();
        cond.translate(rets, params);
        /* TODO: 如果false跳转至结束标签 */
        if (stmt instanceof BlockStmt) {
            Generator.enterScope(WHILE);
            stmt.translate(rets, params);
            Generator.exitScope();
        } else {
            stmt.translate(rets, params);
        }
        Generator.addQuaternion(new Jump(label));
        Generator.addLabel(new Label(label.getName() + "_end"));
        rets.replace("stmtType", "whileStmt");
    }
}
