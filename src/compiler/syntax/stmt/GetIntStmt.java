package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.syntax.Syntax;
import compiler.syntax.exp.LVal;
import compiler.type.SyntaxType;

public class GetIntStmt extends Stmt {
    private LVal lVal;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.LVal) {
            lVal = (LVal) child;
        }
    }
    @Override
    public void translate() {
        super.translate();
        if (lVal.isConst()) {
            ErrorRecorder.insert(new Error(lVal.getRow(), "h"));
        }
    }
}
