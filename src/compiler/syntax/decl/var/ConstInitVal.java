package compiler.syntax.decl.var;

import compiler.representation.Generator;
import compiler.representation.quaternion.Single;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.Var;
import compiler.symbol.entry.ConstEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.exp.ConstExp;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstInitVal extends Nonterminal {
    private ConstExp constExp;
    private final ArrayList<ConstInitVal> constInitVals = new ArrayList<>();

    public ConstInitVal() {
        super(SyntaxType.ConstInitVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case ConstExp:
                constExp = (ConstExp) child;
                break;
            case ConstInitVal:
                constInitVals.add((ConstInitVal) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        super.translate(rets, params);
        if (constExp != null) {
            constExp.translate(rets, params);
            Imm initVal = (Imm) rets.get("dst");
            ConstEntry entry = (ConstEntry) params.get("def");
            entry.addInitVal(initVal.getValue());
            if (!entry.isGlobal()) {
                Generator.addQuaternion(new Single("+", new Var(entry), initVal));
            }
        } else {
            for (ConstInitVal constInitVal : constInitVals) {
                constInitVal.translate(rets, params);
            }
        }
    }
}
