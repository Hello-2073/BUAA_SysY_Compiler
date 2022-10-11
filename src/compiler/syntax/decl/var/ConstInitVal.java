package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class ConstInitVal extends Nonterminal {
    private final ArrayList<ConstInitVal> constInitVals = new ArrayList<>();

    public ConstInitVal() {
        super(SyntaxType.ConstInitVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.ConstInitVal) {
            constInitVals.add((ConstInitVal) child);
        }
    }

    @Override
    public void translate() {
        for (ConstInitVal constInitVal : constInitVals) {
            constInitVal.translate();
        }
    }
}
