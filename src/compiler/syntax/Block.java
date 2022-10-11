package compiler.syntax;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.type.ScopeType;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class Block extends Nonterminal {
    private ArrayList<BlockItem> items = new ArrayList<>();
    private Terminal rbrace;

    public Block() {
        super(SyntaxType.Block);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case RBRACE:
                rbrace = (Terminal) child;
                break;
            case BlockItem:
                items.add((BlockItem) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        if (SymbolTable.getCurrentScopeType() == ScopeType.FUNC && SymbolTable.hasReturnValue()) {
            if (items.size() == 0 || !items.get(items.size()-1).checkReturn()) {
                System.out.println("第 " + rbrace.getRow() + " 行：函数无返回值。");
                ErrorRecorder.insert(new Error(rbrace.getRow(), "g"));
            }
        }
    }
}
