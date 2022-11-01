package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncRParams extends Nonterminal {
    private final ArrayList<Exp> params = new ArrayList<>();

    public FuncRParams() {
        super(SyntaxType.FuncRParams);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.Exp) {
            params.add((Exp) child);
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        ArrayList<Integer> shape = new ArrayList<>();
        ArrayList<Arg> args = new ArrayList<>();
        for (Exp param : this.params) {
            param.translate(rets, params);
            args.add((Arg) rets.get("dst"));
            shape.add((Integer) rets.get("dim"));
        }
        rets.put("args", args);
        rets.put("dims", shape);
    }
}
