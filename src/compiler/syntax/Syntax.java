package compiler.syntax;

public class Syntax {
    private final SyntaxType type;

    public Syntax(SyntaxType type) {
        this.type = type;
    }

    public SyntaxType getType() {
        return type;
    }
}
