package compiler.syntax;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.decl.var.Decl;
import compiler.syntax.stmt.ReturnStmt;
import compiler.syntax.stmt.Stmt;
import compiler.type.SyntaxType;

public class BlockItem extends Nonterminal {
    private Decl decl = null;
    private Stmt stmt = null;

    public BlockItem() {
        super(SyntaxType.BlockItem);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Decl:
                decl = (Decl) child;
                break;
            case Stmt:
                stmt = (Stmt) child;
                break;
            default:
                break;
        }
    }

    public boolean checkReturn() {
        if (stmt == null || !(stmt instanceof ReturnStmt)) {
            return false;
        }
        return ((ReturnStmt) stmt).checkReturn();
    }
}
