package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.ConstExp;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class VarDef extends Nonterminal {
    private Terminal ident;
    private ArrayList<ConstExp> constExps = new ArrayList<>();

    public VarDef() {
        super(SyntaxType.VarDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case IDENFR:
                ident = (Terminal) child;
                break;
            case ConstExp:
                constExps.add((ConstExp) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
    }

    public String getName() {
        return ident.getContent();
    }

    public int getRow() {
        return ident.getRow();
    }

    public int getDim() {
        return constExps.size();
    }
}
