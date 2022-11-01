package compiler.syntax.decl.func;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Imm;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.ConstExp;
import compiler.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.HashMap;

public class FuncFParam extends Nonterminal {
    private Terminal ident;
    private int dim = 0;
    private final ArrayList<ConstExp> constExps = new ArrayList<>();

    public FuncFParam() {
        super(SyntaxType.FuncFParam);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case ConstExp:
                constExps.add((ConstExp) child);
                break;
            case LBRACK:
                dim++;
                break;
            case IDENFR:
                ident = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        ArrayList<Integer> dims = new ArrayList<>();
        if (dim > 0) {
            dims.add(null);
        }
        for (ConstExp constExp : constExps) {
            constExp.translate(rets, params);
            dims.add(((Imm) rets.get("dst")).getValue());
        }
        try {
            Generator.insertFParamSymbol(ident.getContent(), dims);
        } catch (Error e) {
            System.out.println("第 " + ident.getRow() + " 行：重复定义的表示符" + ident.getContent() + "。");
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
    }
}
