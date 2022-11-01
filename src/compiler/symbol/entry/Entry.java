package compiler.symbol.entry;

public class Entry {
    private final String name;
    private final SymbolType type;
    private final boolean isGlobal;

    public Entry(String name, SymbolType type, boolean isGlobal) {
        this.name = name;
        this.type = type;
        this.isGlobal = isGlobal;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }
}
