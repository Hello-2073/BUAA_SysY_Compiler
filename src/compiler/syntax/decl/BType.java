package compiler.syntax.decl;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.Terminal;

import java.util.HashMap;

public class BType extends Nonterminal {
    private Terminal type;

    public BType() {
        super(SyntaxType.BType);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.INTTK) {
            type = (Terminal) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.replace("bType", type.getContent());
    }
}
