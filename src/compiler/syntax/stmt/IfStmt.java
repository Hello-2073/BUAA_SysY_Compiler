package compiler.syntax.stmt;

import compiler.representation.Generator;
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
        cond.translate(rets, params);
        if (stmt instanceof BlockStmt) {
            Generator.enterScope(IF);
            stmt.translate(rets, params);
            Generator.exitScope();
        } else {
            stmt.translate(rets, params);
        }
        if (elseStmt instanceof BlockStmt) {
            Generator.enterScope(IF);
            elseStmt.translate(rets, params);
            Generator.exitScope();
        } else if (elseStmt != null) {
            elseStmt.translate(rets, params);
        }
        rets.replace("stmtType", "ifStmt");
    }
}
