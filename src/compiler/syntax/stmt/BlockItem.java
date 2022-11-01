package compiler.syntax.stmt;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.decl.var.Decl;
import compiler.syntax.stmt.ReturnStmt;
import compiler.syntax.stmt.Stmt;

import java.util.HashMap;

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

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        if (decl == null) {
            stmt.translate(rets, params);
        } else if (stmt == null) {
            decl.translate(rets, params);
        }
    }
}
