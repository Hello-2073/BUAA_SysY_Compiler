package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.Exp;

public class ReturnStmt extends Stmt {
    private Terminal returntk;
    private Exp returnValue = null;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Exp:
                returnValue = (Exp) child;
                break;
            case RETURNTK:
                returntk = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        if (SymbolTable.hasReturnValue()) {
            if (returnValue == null) {
                ErrorRecorder.insert(new Error(returntk.getRow(), "g"));
            }
        } else if (returnValue != null) {
            ErrorRecorder.insert(new Error(returntk.getRow(), "f"));
        }
    }

    public boolean checkReturn() {
        return returnValue != null;
    }
}
