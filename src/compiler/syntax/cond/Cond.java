package compiler.syntax.cond;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class Cond extends Nonterminal {
    public Cond() {
        super(SyntaxType.Cond);
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
