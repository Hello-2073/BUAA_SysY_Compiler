package compiler.syntax.decl.func;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncFParams extends Nonterminal {
    private final List<FuncFParam> fParams = new ArrayList<>();

    public FuncFParams() {
        super(SyntaxType.FuncFParams);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.FuncFParam) {
            fParams.add((FuncFParam) child);
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        for (FuncFParam fParam : fParams) {
            fParam.translate(rets, params);
        }
    }
}
