package compiler.syntax.exp.unaryExp;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.FuncEntry;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.FuncRParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FuncCallUnaryExp extends UnaryExp {

    private Terminal ident = null;
    private FuncRParams rParams = null;
    private FuncEntry funcEntry = null;

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case IDENFR:
                ident = (Terminal) child;
                break;
            case FuncRParams:
                rParams = (FuncRParams) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        try {
            funcEntry = (FuncEntry) SymbolTable.consult(ident.getContent());
            List<Integer> fshape = funcEntry.getFParamsShape();
            List<Integer> rshape;
            if (rParams != null) {
                rshape = rParams.getShape();
            } else {
                rshape = new ArrayList<>();
            }
            System.out.println(rshape + " : " + fshape);
            int paramNum = rshape.size();
            if (fshape.size() != paramNum) {
                ErrorRecorder.insert(new Error(ident.getRow(), "d"));
            } else for (int i = 0; i < paramNum; i++) {
                if (!Objects.equals(rshape.get(i), fshape.get(i))) {
                    ErrorRecorder.insert(new Error(ident.getRow(), "e"));
                }
            }
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("第 " + ident.getRow() + " 行：未定义的标识符" + ident.getContent());
            ErrorRecorder.insert(new Error(ident.getRow(), "c"));
        }
    }

    @Override
    public Integer getDim() {
        if (funcEntry.getFuncType().equals("int")) {
            return 0;
        }
        return null;
    }
}
