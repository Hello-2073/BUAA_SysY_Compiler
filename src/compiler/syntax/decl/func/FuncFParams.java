package compiler.syntax.decl.func;

import compiler.symbol.entry.VarEntry;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.type.SyntaxType;

import java.util.ArrayList;
import java.util.List;

public class FuncFParams extends Nonterminal {
    private final List<FuncFParam> fParams = new ArrayList<>();

    public FuncFParams() {
        super(SyntaxType.FuncFParams);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        if (child.getType() == SyntaxType.FuncFParam) {
            fParams.add((FuncFParam) child);
        }
    }

    public List<VarEntry> getEntries() {
        List<VarEntry> entries = new ArrayList<>();
        for (FuncFParam fParam : fParams) {
            entries.add(fParam.getEntry());
        }
        return entries;
    }
}
