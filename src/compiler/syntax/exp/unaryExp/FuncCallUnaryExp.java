package compiler.syntax.exp.unaryExp;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;
import compiler.representation.quaternion.opnum.RetValue;
import compiler.symbol.entry.FuncEntry;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.FuncRParams;

import java.util.*;
import java.util.List;

public class FuncCallUnaryExp extends UnaryExp {
    private Terminal ident = null;
    private FuncRParams rParams = null;

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
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        try {
            FuncEntry funcEntry = (FuncEntry) Generator.consultSymbol(ident.getContent());
            List<Integer> fshape = funcEntry.getFParamsShape();
            List<Integer> rshape = new ArrayList<>();
            if (rParams != null) {
                params.put("funcName", ident.getContent());
                rParams.translate(rets, params);
                rshape = (List<Integer>) rets.get("dims");
            }
            int paramNum = rshape.size();
            if (fshape.size() != paramNum) {
                ErrorRecorder.insert(new Error(ident.getRow(), "d"));
            } else for (int i = 0; i < paramNum; i++) {
                if (!Objects.equals(rshape.get(i), fshape.get(i))) {
                    ErrorRecorder.insert(new Error(ident.getRow(), "e"));
                }
            }
            Label label = new Label(funcEntry.getName());
            List<Arg> args = (List<Arg>) rets.get("args");
            if (args == null) {
                args = new ArrayList<>();
            }
            Generator.addFunctionCall(label, args);
            Arg dst = Generator.addSingle("+", new RetValue());
            rets.put("dst", dst);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            System.out.println("第 " + ident.getRow() + " 行：未定义的标识符" + ident.getContent());
            ErrorRecorder.insert(new Error(ident.getRow(), "c"));
        }
    }
}
