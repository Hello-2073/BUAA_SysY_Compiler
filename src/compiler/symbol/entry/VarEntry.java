package compiler.symbol.entry;

public class VarEntry extends Entry {
    private final int dim;

    public VarEntry(String name, int dim) {
        super(name);
        this.dim = dim;
    }

    public int getDim() {
        return dim;
    }
}
