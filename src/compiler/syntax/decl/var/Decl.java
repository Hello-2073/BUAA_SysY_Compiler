package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

public class Decl extends Nonterminal {
    public Decl() {
        super(SyntaxType.Decl);
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
