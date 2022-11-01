package compiler.syntax;

public class Terminal extends Syntax {
    private final String content;
    private final int row;

    public Terminal(SyntaxType type, String content, int row) {
        super(type);
        this.content = content;
        this.row = row;
    }

    public String getContent() {
        return content;
    }

    public int getRow() {
        return row;
    }

    @Override
    public String toString() {
        return getType() + " " + content;
    }
}
