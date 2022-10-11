package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;

public class BreakStmt extends Stmt {

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }

    @Override
    public void translate() {
        super.translate();
        if (!SymbolTable.hasInnerLoopScope()) {
            ErrorRecorder.insert(new Error(((Terminal)children.get(0)).getRow(), "m"));
        }
    }
}
