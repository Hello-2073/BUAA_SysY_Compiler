package compiler.syntax.exp;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

import java.util.ArrayList;
import java.util.List;

public class FuncRParams extends Nonterminal {
    private final ArrayList<Exp> params = new ArrayList<>();

    public FuncRParams() {
        super(SyntaxType.FuncRParams);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.Exp) {
            params.add((Exp) child);
        }
    }

    public List<Integer> getShape() {
        ArrayList<Integer> dims = new ArrayList<>();
        for (Exp exp : params) {
            dims.add(exp.getDim());
        }
        return dims;
    }
}
