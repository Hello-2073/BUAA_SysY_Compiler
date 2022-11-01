package compiler.syntax.exp;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Binary;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.OpnumType;
import compiler.representation.quaternion.opnum.Var;
import compiler.symbol.entry.ConstEntry;
import compiler.symbol.entry.Entry;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.HashMap;

public class LVal extends Nonterminal {
    private Terminal ident;
    private final ArrayList<Exp> indices = new ArrayList<>();

    public LVal() {
        super(SyntaxType.LVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case IDENFR:
                ident = (Terminal) child;
                break;
            case Exp:
                indices.add((Exp) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        try {
            VarEntry var = (VarEntry) Generator.consultSymbol(ident.getContent());
            if ("def".equals(params.get("lValUsage")) && var instanceof ConstEntry) {
                System.out.println("第 " + ident.getRow() + " 行：为常量赋值。");
                ErrorRecorder.insert(new Error(ident.getRow(), "h"));
            }
            Arg offset = null;
            if (indices.size() > 0) {
                ArrayList<Integer> weights = var.getWeights();
                for (int i = 0; i < indices.size(); i++) {
                    indices.get(i).translate(rets, params);
                    Arg src1 = (Arg) rets.get("dst");
                    Arg src2 = new Imm(weights.get(i));
                    Arg dst = Generator.addBinary("*", src1, src2);
                    if (offset == null) {
                        offset = dst;
                    } else {
                        offset = Generator.addBinary("+", offset, dst);
                    }
                }
            }
            rets.put("offset", offset);
            rets.put("dim", var.getDim() - indices.size());
            if (var instanceof ConstEntry && (offset == null || offset.getType() == OpnumType.Imm)) {
                int value;
                if (offset == null) {
                    value = var.getInitVal(0);
                } else {
                    value = var.getInitVal(((Imm) offset).getValue());
                }
                rets.put("dst", new Imm(value));
            } else {
                rets.put("dst", new Var(var));
            }
        } catch (Error e) {
            System.out.println("第 " + ident.getRow() + " 行: 未定义的标识符 " + ident.getContent() + "。");
            ErrorRecorder.insert(new Error(ident.getRow(), "c"));
        }
    }
}
