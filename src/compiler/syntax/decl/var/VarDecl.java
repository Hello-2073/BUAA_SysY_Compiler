package compiler.syntax.decl.var;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.decl.BType;

import java.util.ArrayList;
import java.util.HashMap;

public class VarDecl extends Nonterminal {
    private BType bType;
    private final ArrayList<VarDef> varDefs = new ArrayList<>();

    public VarDecl() {
        super(SyntaxType.VarDecl);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.VarDef) {
            varDefs.add((VarDef) child);
        } else if (child.getType() == SyntaxType.BType) {
            bType = (BType) child;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        bType.translate(rets, params);
        params.put("bType", rets.get("bType"));
        for (VarDef varDef : varDefs) {
            varDef.translate(rets, params);
        }
    }
}
