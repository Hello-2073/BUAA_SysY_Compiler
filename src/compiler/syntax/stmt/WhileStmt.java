package compiler.syntax.stmt;

import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Label;
import compiler.syntax.Syntax;
import compiler.syntax.exp.cond.Cond;

import java.util.HashMap;
import java.util.List;

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
        Label whileHead = Generator.allocLabel();
        Label whileTail = Generator.allocLabel();
        Label trueForCond = Generator.allocLabel();
        Label falseForCond = whileTail;

        Generator.addLabel(whileHead);
        params.put("trueForCond", falseForCond);
        params.put("falseForCond", falseForCond);
        cond.translate(rets, params);

        Generator.addLabel(trueForCond);
        Label saveHead = (Label) params.get("whileHead");
        Label saveTail = (Label) params.get("whileTail");
        params.put("whileHead", whileHead);
        params.put("whileTail", whileTail);
        stmt.translate(rets, params);
        params.put("whileHead", saveHead);
        params.put("whileTail", saveTail);
        Generator.addQuaternion(new Jump(whileHead));
        Generator.addLabel(falseForCond);

        rets.replace("stmtType", "whileStmt");
    }
}
