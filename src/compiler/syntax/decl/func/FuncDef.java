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

import java.util.ArrayList;

public class FuncDef extends Nonterminal {
    private FuncType funcType;
    private Terminal ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef() {
        super(SyntaxType.FuncDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case FuncType:
                funcType = (FuncType) child;
                break;
            case IDENFR:
                ident = (Terminal) child;
                break;
            case FuncFParams:
                funcFParams = (FuncFParams) child;
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
        funcType.translate();
        FuncEntry funcEntry = new FuncEntry(ident.getContent(), funcType.getFuncType());
        try {
            SymbolTable.insert(funcEntry);
        } catch (Exception e) {
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
        SymbolTable.enterScope(funcEntry);
        if (funcFParams != null) {
            funcFParams.translate();
            funcEntry.setFParam(funcFParams.getEntries());
        } else {
            funcEntry.setFParam(new ArrayList<>());
        }
        block.translate();
        SymbolTable.exitScope();
    }
}
