package compiler.syntax.stmt;

import compiler.symbol.SymbolTable;
import compiler.syntax.Block;
import compiler.syntax.Syntax;
import compiler.type.ScopeType;
import compiler.type.SyntaxType;

public class BlockStmt extends Stmt {
    private Block block;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.Block) {
            block = (Block) child;
        }
    }

    @Override
    public void translate() {
        SymbolTable.enterScope(ScopeType.BASIC);
        block.translate();
        SymbolTable.exitScope();
    }
}
