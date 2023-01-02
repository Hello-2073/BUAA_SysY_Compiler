package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Jump;
import compiler.representation.quaternion.opnum.Arg;
import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;

import java.util.HashMap;

public class ContinueStmt extends Stmt {

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.replace("stmtType", "continueStmt");
        Arg label = (Arg) params.get("whileHead");
        if (label == null) {
            int row = ((Terminal)children.get(0)).getRow();
            System.out.println("第 " +  row + " 行: continue语句无任何外层循环体");
            ErrorRecorder.insert(new Error(((Terminal)children.get(0)).getRow(), "m"));
        }
        Generator.addQuaternion(new Jump(label));
    }
}
