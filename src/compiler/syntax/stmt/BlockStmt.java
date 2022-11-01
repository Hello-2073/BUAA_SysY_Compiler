package compiler.syntax.stmt;

import compiler.representation.Generator;
import compiler.syntax.Syntax;
import compiler.symbol.scope.ScopeType;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

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
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        Generator.enterScope(ScopeType.BASIC);
        block.translate(rets, params);
        Generator.exitScope();
    }
}
