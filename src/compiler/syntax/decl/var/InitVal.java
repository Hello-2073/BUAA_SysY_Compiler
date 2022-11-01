package compiler.syntax.decl.var;

import compiler.representation.Generator;
import compiler.representation.quaternion.Single;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.OpnumType;
import compiler.representation.quaternion.opnum.Var;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.exp.Exp;

import java.util.ArrayList;
import java.util.HashMap;

public class InitVal extends Nonterminal {
    private Exp exp;
    private final ArrayList<InitVal> initVals = new ArrayList<>();

    public InitVal() {
        super(SyntaxType.InitVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Exp:
                exp = (Exp) child;
                break;
            case InitVal:
                initVals.add((InitVal) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        super.translate(rets, params);
        if (exp != null) {
            exp.translate(rets, params);
            Arg initVal = (Arg) rets.get("dst");
            VarEntry entry = (VarEntry) params.get("def");
            if (!entry.isGlobal()) {
                Generator.addQuaternion(new Single("+", new Var(entry), initVal));
            } else if (initVal.getType() == OpnumType.Imm) {
                entry.addInitVal(((Imm) initVal).getValue());
            } else {
                entry.addInitVal(0);
            }
        } else for (InitVal initVal : initVals) {
            initVal.translate(rets, params);
        }
    }
}
