package compiler.syntax.decl.var;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class VarDecl extends Nonterminal {
    private final ArrayList<VarDef> varDefs = new ArrayList<>();

    public VarDecl() {
        super(SyntaxType.VarDecl);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.VarDef) {
            varDefs.add((VarDef) child);
        }
    }

    @Override
    public void translate() {
        super.translate();
        for (VarDef varDef : varDefs) {
            try {
                SymbolTable.insert(new VarEntry(varDef.getName(), varDef.getDim()));
            } catch (Exception e) {
                System.out.println("第 " + varDef.getRow() + " 行：重复定义的标识符 " + varDef.getName() + "。");
                ErrorRecorder.insert(new Error(varDef.getRow(), "b"));
            }
        }
    }
}
