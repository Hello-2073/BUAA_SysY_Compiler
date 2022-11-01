package compiler.syntax.decl.func;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class FuncType extends Nonterminal {
    private Terminal funcType;

    public FuncType() {
        super(SyntaxType.FuncType);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        funcType = (Terminal) child;
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.put("funcType", funcType.getContent());
    }
}
