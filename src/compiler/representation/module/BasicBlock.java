package compiler.representation.module;

import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Label;

import java.util.ArrayList;
import java.util.List;

public class BasicBlock {
    private final Label label;
    private final List<Quaternion> quaternions = new ArrayList<>();

    public BasicBlock(Label label) {
        this.label = label;
    }

    public void addQuaternion(Quaternion quaternion) {
        quaternions.add(quaternion);
    }

    public List<Quaternion> getQuaternions() {
        return quaternions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (label != null) {
            sb.append(label);
            sb.append(": ");
        }
        for (Quaternion quaternion : quaternions) {
            sb.append("\n\t");
            sb.append(quaternion);
        }
        return sb.toString();
    }
}
