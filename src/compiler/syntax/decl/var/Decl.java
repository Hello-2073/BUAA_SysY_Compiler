package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class Decl extends Nonterminal {
    public Decl() {
        super(SyntaxType.Decl);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        super.translate(rets, params);
    }
}
