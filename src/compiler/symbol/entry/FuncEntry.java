package compiler.symbol.entry;

import java.util.ArrayList;
import java.util.List;

public class FuncEntry extends Entry {
    private final String funcType;

    private List<VarEntry> fParams;

    public FuncEntry(String name, String funcType) {
        super(name);
        this.funcType = funcType;
    }

    public void setFParam(List<VarEntry> fParams) {
        this.fParams = fParams;
    }

    public String getFuncType() {
        return funcType;
    }

    public List<Integer> getFParamsShape() {
        ArrayList<Integer> shape = new ArrayList<>();
        for (VarEntry fParam : fParams) {
            shape.add(fParam.getDim());
        }
        return shape;
    }
}
