package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Save extends Quaternion {
    public Save(Arg dst, Arg src1, Arg src2) {
        super(Operations.SAVE, dst, src1, src2);
    }

    @Override
    public String toString() {
        return getDst() + "[" + getSrc1() + "] = " + getSrc2();
    }
}
