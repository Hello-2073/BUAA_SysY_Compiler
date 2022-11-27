package compiler.symbol.entry;

import java.util.ArrayList;
import java.util.List;

public class FuncEntry extends Entry {
    private final String funcType;
    private final List<VarEntry> fParams = new ArrayList<>();

    public FuncEntry(String name, String funcType) {
        super(name, SymbolType.FUNC, true);
        this.funcType = funcType;
    }

    public List<VarEntry> getFParams() {
        return fParams;
    }

    public int getFParamNum() {
        return fParams.size();
    }

    public void addFParam(VarEntry fParam) {
        fParams.add(fParam);
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
