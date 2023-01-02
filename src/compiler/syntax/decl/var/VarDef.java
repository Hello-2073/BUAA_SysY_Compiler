package compiler.syntax.decl.var;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Read;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.Var;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.ConstExp;
import compiler.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.HashMap;

public class VarDef extends Nonterminal {
    private Terminal ident = null;
    private final ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal initVal = null;
    private boolean getInt = false;

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
            case InitVal:
                initVal = (InitVal) child;
                break;
            case GETINTTK:
                getInt = true;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        try {
            ArrayList<Integer> ranges = new ArrayList<>();
            for (ConstExp constExp : constExps) {
                constExp.translate(rets, params);
                ranges.add(((Imm)rets.get("dst")).getValue());
            }
            VarEntry varEntry = Generator.insertVarSymbol(ident.getContent(), ranges);
            if (initVal != null) {
                params.put("def", varEntry);
                initVal.translate(rets, params);
            }
            if (getInt) {
                Generator.addQuaternion(new Read(new Var(varEntry)));
            }
        } catch (Error e) {
            System.out.println("第 " + ident.getRow() + " 行: 重定义的符号" + ident.getContent());
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
    }
}
