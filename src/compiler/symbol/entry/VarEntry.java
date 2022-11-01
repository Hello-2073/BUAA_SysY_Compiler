package compiler.symbol.entry;

import java.util.ArrayList;
import java.util.List;

public class VarEntry extends Entry {
    protected final ArrayList<Integer> shape;
    protected final ArrayList<Integer> weights;
    protected int offset;

    private final List<Integer> initVals = new ArrayList<>();

    public VarEntry(String name, ArrayList<Integer> shape, boolean isGlobal) {
        super(name, SymbolType.VAR, isGlobal);
        this.shape = shape;
        this.weights = new ArrayList<>(shape);
        if (shape.size() > 0) {
            weights.set(weights.size() - 1, 1);
            for (int i = weights.size() - 2; i >= 0; i--) {
                weights.set(i, shape.get(i + 1) * weights.get(i + 1));
            }
        }
    }

    public void addInitVal(int val) {
        initVals.add(val);
    }

    public int getInitVal(int index) {
        if (index > initVals.size()) {
            return 0;
        }
        return initVals.get(index);
    }

    public List<Integer> getInitVals() {
        return initVals;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public int size() {
        int size = 4;
        if (shape != null) {
            for (int n : shape) {
                size *= n;
            }
        }
        return size;
    }

    public int getDim() {
        return shape.size();
    }

    public ArrayList<Integer> getWeights() {
        return weights;
    }
}
