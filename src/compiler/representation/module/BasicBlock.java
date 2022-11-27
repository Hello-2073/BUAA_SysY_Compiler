package compiler.representation.module;

import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;
import compiler.representation.quaternion.opnum.Tmp;
import compiler.representation.quaternion.opnum.Var;

import java.util.*;


public class BasicBlock {
    private final Label label;
    private final List<Quaternion> quaternions;
    private final Set<Arg> use;
    private final Set<Arg> def;

    public BasicBlock(Label label) {
        this.label = label;
        this.quaternions = new ArrayList<>();
        this.use = new HashSet<>();
        this.def = new HashSet<>();
    }

    public void addQuaternion(Quaternion quaternion) {
        quaternions.add(quaternion);
        Arg dst = quaternion.getDst();
        Arg src1 = quaternion.getSrc1();
        Arg src2 = quaternion.getSrc2();
        if (dst instanceof Var && !use.contains(dst)) {
            def.add(dst);
        }
        if (src1 instanceof Var && !def.contains(src1)) {
            use.add(src1);
        }
        if (src1 instanceof Var && !def.contains(src2)) {
            use.add(src2);
        }
    }

    public HashMap<Tmp, Quaternion> getTmpReleaseMap() {
        HashMap<Tmp, Quaternion> res = new HashMap<>();
        for (Quaternion quaternion : quaternions) {
            if (quaternion.getSrc1() instanceof Tmp) {
                res.put((Tmp) quaternion.getSrc1(), quaternion);
            }
            if (quaternion.getSrc2() instanceof Var) {
                res.put((Tmp) quaternion.getSrc2(), quaternion);
            }
        }
        return res;
    }

    public Set<Arg> getDef() {
        return def;
    }

    public Set<Arg> getUse() {
        return use;
    }

    public List<Quaternion> getQuaternions() {
        return quaternions;
    }

    public Label getLabel() {
        return label;
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
