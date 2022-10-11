package compiler.syntax.stmt;

import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.cond.Cond;

import static compiler.type.ScopeType.WHILE;

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
    public void translate() {
        cond.translate();
        if (stmt instanceof BlockStmt) {
            SymbolTable.enterScope(WHILE);
            stmt.translate();
            SymbolTable.exitScope();
        } else {
            stmt.translate();
        }
    }
}
