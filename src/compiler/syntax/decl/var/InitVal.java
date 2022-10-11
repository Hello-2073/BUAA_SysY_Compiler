package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class InitVal extends Nonterminal {
    public InitVal() {
        super(SyntaxType.InitVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }

    @Override
    public void translate() {
        super.translate();
    }
}
