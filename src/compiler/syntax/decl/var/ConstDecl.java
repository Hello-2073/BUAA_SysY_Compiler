package compiler.syntax.decl.var;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.ConstEntry;
import compiler.symbol.entry.Entry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

import java.util.ArrayList;


public class ConstDecl extends Nonterminal {
    private final ArrayList<ConstDef> constDefs = new ArrayList<>();

    public ConstDecl() {
        super(SyntaxType.ConstDecl);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.ConstDef) {
            constDefs.add((ConstDef) child);
        }
    }

    @Override
    public void translate() {
        super.translate();
        for (ConstDef constDef : constDefs) {
            try {
                SymbolTable.insert(new ConstEntry(constDef.getName(), constDef.getDim()));
            } catch (Exception e) {
                System.out.println("第 " + constDef.getRow() + " 行：重复定义的标识符 " + constDef.getName() + "。");
                ErrorRecorder.insert(new Error(constDef.getRow(), "b"));
            }
        }
    }
}
