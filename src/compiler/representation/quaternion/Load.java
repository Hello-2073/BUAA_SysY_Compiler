package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;

public class Load extends Quaternion {
    public Load(Arg dst, Arg src1, Arg src2) {
        super(Operations.LOAD, dst, src1, src2);
    }

    @Override
    public String toString() {
        return getDst() + " = " + getSrc1() + "[" + getSrc2() + "]";
    }
}
