package compiler.syntax.decl.var;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Imm;
import compiler.symbol.entry.ConstEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;
import compiler.syntax.exp.ConstExp;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstDef extends Nonterminal {
    private Terminal ident;
    private final ArrayList<ConstExp> indices = new ArrayList<>();
    private ConstInitVal constInitVal;

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
            case ConstExp:
                indices.add((ConstExp) child);
                break;
            case ConstInitVal:
                constInitVal = (ConstInitVal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        try {
            ArrayList<Integer> ranges = new ArrayList<>();
            for (ConstExp index : indices) {
                index.translate(rets, params);
                Imm n = (Imm) rets.get("dst");
                ranges.add(n.getValue());
            }
            ConstEntry entry = Generator.insertConstSymbol(ident.getContent(), ranges);
            params.put("def", entry);
            constInitVal.translate(rets, params);
        } catch (Error e) {
            System.out.println("-第 " + ident.getRow() + " 行: 重定义的符号" + ident.getContent());
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
    }
}
