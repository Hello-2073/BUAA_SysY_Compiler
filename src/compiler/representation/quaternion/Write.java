package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Write extends Quaternion {
    private final Arg src;

    public Write(Arg src1) {
        super(Operations.WRITE, null, src1, null);
        src = src1;
    }

    public Arg getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return "print(" + src + ")";
    }
}
