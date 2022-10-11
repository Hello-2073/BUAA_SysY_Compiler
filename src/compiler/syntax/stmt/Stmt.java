package compiler.syntax.stmt;

import compiler.syntax.*;
import compiler.type.SyntaxType;

public abstract class Stmt extends Nonterminal {
    public Stmt() {
        super(SyntaxType.Stmt);
    }
}
