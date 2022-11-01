package compiler.syntax.decl.var;

import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.decl.BType;

import java.util.ArrayList;
import java.util.HashMap;


public class ConstDecl extends Nonterminal {
    private BType bType;
    private final ArrayList<ConstDef> constDefs = new ArrayList<>();

    public ConstDecl() {
        super(SyntaxType.ConstDecl);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.ConstDef) {
            constDefs.add((ConstDef) child);
        } else if (child.getType() == SyntaxType.BType) {
            bType = (BType) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        bType.translate(rets, params);
        params.put("bType", rets.get("bType"));
        for (ConstDef constDef : constDefs) {
            constDef.translate(rets, params);
        }
    }
}
