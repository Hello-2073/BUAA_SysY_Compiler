package compiler.syntax;

import compiler.syntax.decl.var.Decl;
import compiler.syntax.decl.func.FuncDef;
import compiler.syntax.decl.func.MainFuncDef;

import java.util.ArrayList;

import static compiler.type.SyntaxType.*;

public class CompileUnit extends Nonterminal {
    private ArrayList<Decl> decls = new ArrayList<>();
    private ArrayList<FuncDef> funcDefs = new ArrayList<>();
    private MainFuncDef mainFuncDef;

    public CompileUnit() {
        super(CompUnit);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case Decl:
                decls.add((compiler.syntax.decl.var.Decl) child);
                break;
            case FuncDef:
                funcDefs.add((compiler.syntax.decl.func.FuncDef) child);
                break;
            case MainFuncDef:
                mainFuncDef = (compiler.syntax.decl.func.MainFuncDef) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        for (Syntax syntax : children) {
            if (syntax instanceof Nonterminal) {
                ((Nonterminal) syntax).translate();
            }
        }
    }
}
