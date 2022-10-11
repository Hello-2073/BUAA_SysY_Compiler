package compiler.syntax.exp;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.ConstEntry;
import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.type.SyntaxType;

import java.util.ArrayList;

public class LVal extends Nonterminal implements Calculable {
    private Terminal ident;
    private final ArrayList<Exp> indices = new ArrayList<>();

    private int dim;
    private VarEntry varEntry;

    public LVal() {
        super(SyntaxType.LVal);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case IDENFR:
                ident = (Terminal) child;
                break;
            case Exp:
                indices.add((Exp) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        super.translate();
        try {
            varEntry = (VarEntry) SymbolTable.consult(ident.getContent());
            dim = varEntry.getDim() - indices.size();
        } catch (Exception e) {
            System.out.println("第 " + ident.getRow() + " 行: 未定义的标识符 " + ident.getContent() + "。");
            ErrorRecorder.insert(new Error(ident.getRow(), "c"));
        }
    }

    @Override
    public Integer getDim() {
        return dim;
    }

    public boolean isConst() {
        return varEntry instanceof ConstEntry;
    }

    public int getRow() {
        return ident.getRow();
    }
}
