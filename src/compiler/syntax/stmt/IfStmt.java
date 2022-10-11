package compiler.syntax.stmt;

import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.cond.Cond;

import static compiler.type.ScopeType.IF;

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
    public void translate() {
        cond.translate();
        if (stmt instanceof BlockStmt) {
            SymbolTable.enterScope(IF);
            stmt.translate();
            SymbolTable.exitScope();
        } else {
            stmt.translate();
        }
        if (elseStmt instanceof BlockStmt) {
            SymbolTable.enterScope(IF);
            elseStmt.translate();
            SymbolTable.exitScope();
        } else if (elseStmt != null) {
            elseStmt.translate();
        }
    }
}
