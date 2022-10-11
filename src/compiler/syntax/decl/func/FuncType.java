package compiler.syntax.decl.func;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

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

    public String getFuncType() {
        return funcType.getContent();
    }
}
