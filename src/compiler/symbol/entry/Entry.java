package compiler.symbol.entry;

public class Entry {
    private final String name;

    public Entry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "name='" + name + '\'' +
                '}';
    }
}
