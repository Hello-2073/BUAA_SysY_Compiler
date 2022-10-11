package compiler.syntax.decl.func;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.FuncEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.Block;
import compiler.type.SyntaxType;

public class MainFuncDef extends Nonterminal {
    private Terminal maintk;
    private Block block;

    public MainFuncDef() {
        super(SyntaxType.MainFuncDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case MAINTK:
                maintk = (Terminal) child;
                break;
            case Block:
                block = (Block) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        FuncEntry funcEntry = new FuncEntry("main", "int");
        try {
            SymbolTable.insert(funcEntry);
        } catch (Exception e) {
            ErrorRecorder.insert(new Error(maintk.getRow(), "b"));
        }
        SymbolTable.enterScope(funcEntry);;
        block.translate();
        SymbolTable.exitScope();
    }
}
