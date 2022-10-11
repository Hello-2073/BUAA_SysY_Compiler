package compiler.syntax.decl.func;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.ConstExp;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class FuncFParam extends Nonterminal {
    private Terminal ident;
    private int dim = 0;
    private ArrayList<ConstExp> constExps = new ArrayList<>();

    private VarEntry entry;

    public FuncFParam() {
        super(SyntaxType.FuncFParam);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case ConstExp:
                constExps.add((ConstExp) child);
                break;
            case LBRACK:
                dim++;
                break;
            case IDENFR:
                ident = (Terminal) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        entry = new VarEntry(ident.getContent(), dim);
        try {
            SymbolTable.insert(entry);
        } catch (Exception e) {
            System.out.println("第 " + ident.getRow() + " 行：重复定义的表示符" + ident.getContent() + "。");
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
    }

    public VarEntry getEntry() {
        return entry;
    }
}
