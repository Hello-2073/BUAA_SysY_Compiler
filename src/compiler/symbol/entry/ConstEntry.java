package compiler.symbol.entry;

import java.util.ArrayList;
import java.util.List;

public class ConstEntry extends VarEntry {
    private final List<Integer> shape;
    private final List<Integer> weight;

    public ConstEntry(String name, ArrayList<Integer> shape, boolean isGlobal) {
        super(name, shape, isGlobal, false);
        this.shape = shape;
        weight = new ArrayList<>(shape);
        if (shape.size() > 0) {
            weight.set(weight.size() - 1, 1);
            for (int i = weight.size() - 2; i >= 0; i--) {
                weight.set(i, shape.get(i + 1) * weight.get(i + 1));
            }
        }
    }

    @Override
    public SymbolType getType() {
        return SymbolType.CONST;
    }
}
