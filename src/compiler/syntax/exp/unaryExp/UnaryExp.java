package compiler.syntax.exp.unaryExp;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Arg;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.exp.UnaryOp;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class UnaryExp extends Nonterminal {
    private UnaryOp op;
    private UnaryExp exp;

    public UnaryExp() {
        super(SyntaxType.UnaryExp);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case UnaryOp:
                op = (UnaryOp) child;
                break;
            case UnaryExp:
                exp = (UnaryExp) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        op.translate(rets, params);
        String op = (String) rets.get("op");
        exp.translate(rets, params);
        Arg src = (Arg) rets.get("dst");
        Arg dst = Generator.addSingle(op, src);
        rets.put("dst", dst);
    }
}
