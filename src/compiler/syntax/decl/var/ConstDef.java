package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class ConstDef extends Nonterminal {
    private Terminal ident;
    private ArrayList<ConstInitVal> constInitVals = new ArrayList<>();

    public ConstDef() {
        super(SyntaxType.ConstDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case IDENFR:
                ident = (Terminal) child;
                break;
            case ConstInitVal:
                constInitVals.add((ConstInitVal) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        for (ConstInitVal constInitVal : constInitVals) {
            constInitVal.translate();
        }
    }

    public String getName() {
        return ident.getContent();
    }

    public int getRow() {
        return ident.getRow();
    }

    public int getDim() {
        return constInitVals.size();
    }
}
