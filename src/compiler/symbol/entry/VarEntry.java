package compiler.symbol.entry;

import java.util.ArrayList;
import java.util.List;

public class VarEntry extends Entry {
    protected final ArrayList<Integer> shape;
    protected final ArrayList<Integer> weights;

    private boolean isFParam;
    private final List<Integer> initVals = new ArrayList<>();

    public VarEntry(String name, ArrayList<Integer> shape, boolean isGlobal, boolean isFParam) {
        super(name, SymbolType.VAR, isGlobal);
        this.isFParam = isFParam;
        this.shape = shape;
        this.weights = new ArrayList<>(shape);
        if (shape.size() > 0) {
            weights.set(weights.size() - 1, 1);
            for (int i = weights.size() - 2; i >= 0; i--) {
                weights.set(i, shape.get(i + 1) * weights.get(i + 1));
            }
        }
    }

    public boolean isFParam() {
        return isFParam;
    }

    public ArrayList<Integer> getShape() {
        return shape;
    }

    public void addInitVal(int val) {
        initVals.add(val);
    }

    public List<Integer> getInitVals() {
        return initVals;
    }

    public int getInitVal(int index) {
        return initVals.get(index);
    }

    public int size() {
        int size = 4;
        for (Integer n : shape) {
            if (n == null) {
                return 4;
            }
            size *= n;
        }
        return size;
    }

    public int getDim() {
        return shape.size();
    }

    public ArrayList<Integer> getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        if (shape.size() > 0) {
            sb.append("[]");
            for (int i = 1; i < shape.size(); i++) {
                sb.append("[").append(shape.get(i)).append("]");
            }
        }
        return sb.toString();
    }
}
