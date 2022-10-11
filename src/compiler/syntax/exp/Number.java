package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

public class Number extends Nonterminal implements Calculable {

    private int value;

    public Number() {
        super(SyntaxType.Number);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
    }

    @Override
    public void translate() {
        super.translate();
        value = Integer.parseInt(((Terminal) children.get(0)).getContent());
    }

    public int getValue() {
        return value;
    }

    @Override
    public Integer getDim() {
        return 0;
    }
}
