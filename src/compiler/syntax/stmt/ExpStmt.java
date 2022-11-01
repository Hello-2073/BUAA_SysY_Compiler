package compiler.syntax.stmt;

import compiler.syntax.Syntax;

import java.util.HashMap;

public class ExpStmt extends Stmt {

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }


    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        super.translate(rets, params);
        rets.replace("stmtType", "expStmt");
    }
}
