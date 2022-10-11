package compiler.syntax.decl;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class BType extends Nonterminal {
    public BType() {
        super(SyntaxType.BType);
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
