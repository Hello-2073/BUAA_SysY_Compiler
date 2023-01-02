package compiler.representation.module;

import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;
import compiler.representation.quaternion.opnum.Tmp;
import compiler.representation.quaternion.opnum.Var;

import java.util.*;


public class BasicBlock {
    private final Label blockName;
    private final List<Quaternion> quaternions;
    private final Set<Arg> useSet;
    private final Set<Arg> defSet;

    private final HashSet<Tmp> tmpVars;
    private final HashMap<Tmp, Integer> tmpDefMap;
    private final HashMap<Tmp, Integer> tmpUseMap;
    private final HashMap<Tmp, Integer> tmpUseTime;

    public BasicBlock(Label blockName) {
        this.blockName = blockName;
        this.quaternions = new ArrayList<>();
        this.useSet = new HashSet<>();
        this.defSet = new HashSet<>();
        this.tmpVars = new HashSet<>();
        this.tmpDefMap = new HashMap<>();
        this.tmpUseMap = new HashMap<>();
        this.tmpUseTime = new HashMap<>();
    }

    public void addQuaternion(Quaternion quaternion) {
        quaternions.add(quaternion);
        Arg dst = quaternion.getDst();
        Arg src1 = quaternion.getSrc1();
        Arg src2 = quaternion.getSrc2();
        recordDef(quaternion, dst);
        recordUse(quaternion, src1);
        recordUse(quaternion, src2);
    }

    private void recordDef(Quaternion quaternion, Arg dst) {
        if (dst instanceof Var && !useSet.contains(dst)) {
            defSet.add(dst);
        } else if (dst instanceof Tmp) {
            tmpVars.add((Tmp) dst);
            tmpUseTime.put((Tmp) dst, 1);
            tmpDefMap.put((Tmp) dst, quaternions.indexOf(quaternion));
            tmpUseMap.put((Tmp) dst, quaternions.indexOf(quaternion));
        }
    }

    private void recordUse(Quaternion quaternion, Arg src) {
        if (src instanceof Var && !defSet.contains(src)) {
            useSet.add(src);
        } else if (src instanceof Tmp) {
            assert tmpUseTime.containsKey(src);
            tmpUseTime.replace((Tmp) src, tmpUseTime.get(src) + 1);
            tmpUseMap.put((Tmp) src, quaternions.indexOf(quaternion));
        }
    }

    public List<Set<Tmp>> getTmpGroups() {
        List<Set<Tmp>> res = new ArrayList<>();
        Set<Tmp> tmps = new HashSet<>(tmpVars);
        while (tmps.size() > 0) {
            Set<Tmp> group = selectOneTmpGroup(tmps);
            tmps.removeAll(group);
            res.add(group);
        }
//                for (Tmp tmp : tmps) {
//                    Set<Tmp> group = new HashSet<>();
//                    group.add(tmp);
//                    res.add(group);
//                }
        return res;
    }

    private Set<Tmp> selectOneTmpGroup(Set<Tmp> tmpVars) {
        /* TODO : 从tmpVars中选择不冲突的一组，使它们的使用次数之和最大 */
        /* 对于每个tmp，生命周期为[t0, t1)，使用次数为cnt */
        ArrayList<Tmp> tmps = new ArrayList<>(tmpVars);
        tmps.sort(Comparator.comparingInt(tmpUseMap::get));
        ArrayList<Tmp> prev = new ArrayList<>();
        for (Tmp tmp : tmps) {
            int beginTime = tmpDefMap.get(tmp);
            if (tmpUseMap.get(tmps.get(0)) > beginTime) {
                prev.add(null);
            } else if (tmpUseMap.get(tmps.get(tmps.size() - 1)) <= beginTime) {
                prev.add(tmps.get(tmps.size() - 1));
            } else {
                int l = 0, r = tmps.size() - 1;
                while(r - l > 1) {
                    int m = (l + r) / 2;
                    if (beginTime >= tmpUseMap.get(tmps.get(m))) {
                        l = m;
                    } else {
                        r = m;
                    }
                }
                prev.add(tmps.get(l));
            }
        }
        HashSet<Tmp> rec = new HashSet<>();
        ArrayList<Integer> w = new ArrayList<>();
        rec.add(tmps.get(0));
        w.add(tmpUseTime.get(tmps.get(0)));
        for (int i = 1; i < tmps.size(); i++) {
            int a = tmpUseTime.get(tmps.get(i));
            if (prev.get(i) != null && !prev.get(i).equals(tmps.get(i))) {
                a += w.get(tmps.indexOf(prev.get(i)));
            }
            int b = w.get(i - 1);
            if (a > b) {
                rec.add(tmps.get(i));
                w.add(a);
            } else {
                w.add(b);
            }
        }
        HashSet<Tmp> res = new HashSet<>();
        for (int i = tmps.size() - 1; i >= 0; ) {
            Tmp tmp = tmps.get(i);
            if (rec.contains(tmp)) {
                res.add(tmp);
                int pre = tmps.indexOf(prev.get(i));
                i = pre == i ? i - 1: pre;
            } else {
                i--;
            }
        }
        return res;
    }

    public Set<Arg> getDefSet() {
        return defSet;
    }

    public Set<Arg> getUseSet() {
        return useSet;
    }

    public List<Quaternion> getQuaternions() {
        return quaternions;
    }

    public Label getBlockName() {
        return blockName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (blockName != null) {
            sb.append(blockName);
            sb.append(": ");
        }
        for (Quaternion quaternion : quaternions) {
            sb.append("\n\t");
            sb.append(quaternion);
        }
        return sb.toString();
    }
}
