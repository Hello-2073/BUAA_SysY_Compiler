package compiler.representation.module;

import compiler.representation.quaternion.Call;
import compiler.representation.quaternion.Push;
import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Label;
import compiler.representation.quaternion.opnum.Var;
import compiler.symbol.entry.FuncEntry;
import compiler.symbol.entry.VarEntry;

import java.util.ArrayList;

public class Function {
    private final FuncEntry funcEntry;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private final ArrayList<FuncEntry> called = new ArrayList<>();
    private final ArrayList<Var> localVars = new ArrayList<>();

    private boolean isLeaf = true;
    private int maxArgNum = 0;


    private BasicBlock curBasicBlock;

    private final ArrayList<Call> calls = new ArrayList<>();

    public Function(FuncEntry funcEntry) {
        this.funcEntry = funcEntry;
        newBasicBlock(null);
    }

    public boolean isLeaf() {
        return calls.size() == 0;
    }

    public String getName() {
        return funcEntry.getName();
    }

    public int getFParaNum() {
        return funcEntry.getFParamNum();
    }

    public Var getFParam(int i) {
        return new Var(funcEntry.getFParams().get(i));
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public void addCalled(FuncEntry funcEntry) {
        called.add(funcEntry);
    }

    public void newBasicBlock(Label tag) {
        BasicBlock basicBlock = new BasicBlock(tag);
        basicBlocks.add(basicBlock);
        curBasicBlock = basicBlock;
    }

    public void addQuaternion(Quaternion quaternion) {
        curBasicBlock.addQuaternion(quaternion);
        if (quaternion instanceof Call) {
            calls.add((Call) quaternion);
        } else if (quaternion instanceof Push) {
            maxArgNum = Math.max(maxArgNum, ((Push) quaternion).getN() + 1);
        }
    }

    public ArrayList<Call> getCalls() {
        return calls;
    }

    public int getMaxArgNum() {
        return maxArgNum;
    }

    public void addLocalVar(Var localVar) {
        localVars.add(localVar);
    }

    public ArrayList<Var> getLocalVars() {
        return localVars;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcEntry.getName());
        sb.append("(");
        for (VarEntry fParam : funcEntry.getFParams()) {
            sb.append(fParam.getName());
            sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.delete(sb.length()-1, sb.length());
        }
        sb.append("):");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock);
            sb.append('\n');
        }
        return sb.toString();
    }
}
