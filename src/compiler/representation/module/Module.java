package compiler.representation.module;

import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Label;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.FuncEntry;
import compiler.symbol.entry.VarEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class Module {
    private Function curFunc;
    private final ArrayList<Function> functions = new ArrayList<>();
    private final HashMap<Label, String> constStr = new HashMap<>();

    private final SymbolTable symbolTable;

    public Module(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public ArrayList<VarEntry> globalVars() {
        return symbolTable.getGlobalVars();
    }

    public void addStr(Label label, String str) {
        constStr.put(label, str);
    }

    public HashMap<Label, String> getConstStr() {
        return constStr;
    }

    public void newFunc(FuncEntry funcEntry) {
        Function function = new Function(funcEntry);
        functions.add(function);
        curFunc = function;
    }

    public void newBlock(Label tag) {
        curFunc.newBasicBlock(tag);
    }

    public void addQuaternion(Quaternion quaternion) {
        curFunc.addQuaternion(quaternion);
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Label label : constStr.keySet()) {
            sb.append(label);
            sb.append(" = \"");
            String str = constStr.get(label);
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '\n') {
                    sb.append("\\n");
                } else {
                    sb.append(str.charAt(i));
                }
            }
            sb.append("\"\n");
        }
        sb.append("\n");
        for (Function function : functions) {
            sb.append(function.toString());
            sb.append('\n');
        }
        return sb.toString();
    }
}
