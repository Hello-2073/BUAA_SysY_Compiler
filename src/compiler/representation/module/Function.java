package compiler.representation.module;

import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Label;
import compiler.symbol.entry.FuncEntry;

import java.util.ArrayList;

public class Function {
    private final FuncEntry funcEntry;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private final ArrayList<FuncEntry> called = new ArrayList<>();

    private boolean isLeaf = true;

    private BasicBlock curBasicBlock;

    public Function(FuncEntry funcEntry) {
        this.funcEntry = funcEntry;
        newBasicBlock(null);
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getName() {
        return funcEntry.getName();
    }

    public int getStackSpace() {
        /*
            stack = [$ra, $fp, 局部变量们, 调用函数的参数们]
        */
        return funcEntry.getSpace() + 8;
    }

    public int getFParaNum() {
        return funcEntry.getFParamNum();
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
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(funcEntry.getName());
        sb.append(":");
        for (BasicBlock basicBlock : basicBlocks) {
            sb.append(basicBlock);
            sb.append('\n');
        }
        return sb.toString();
    }
}
