package compiler.syntax.exp;

import compiler.representation.Generator;
import compiler.representation.quaternion.opnum.Imm;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class Number extends Nonterminal {
    private Terminal intConst;

    public Number() {
        super(SyntaxType.Number);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.INTCON) {
            intConst = (Terminal) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.put("dst", new Imm(intConst.getContent()));
        rets.put("dim", 0);
    }
}
