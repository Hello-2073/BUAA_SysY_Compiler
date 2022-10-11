package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.syntax.Syntax;
import compiler.syntax.exp.Exp;
import compiler.syntax.exp.LVal;

public class AssignStmt extends Stmt {
    private LVal lVal;
    private Exp exp;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case LVal:
                lVal = (LVal) child;
                break;
            case Exp:
                exp = (Exp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        if (lVal.isConst()) {
            System.out.println("第 " + lVal.getRow() + " 行：为常量赋值。");
            ErrorRecorder.insert(new Error(lVal.getRow(), "h"));
        }
    }
}
