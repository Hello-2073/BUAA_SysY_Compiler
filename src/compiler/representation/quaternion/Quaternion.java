package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Quaternion {
    private final Operations type;
    private final Arg dst;
    private final Arg src1;
    private final Arg src2;
    private String comment;

    public Quaternion(Operations operations, Arg dst, Arg src1, Arg src2) {
        type = operations;
        this.dst = dst;
        this.src1 = src1;
        this.src2 = src2;
    }

    public Operations getType() {
        return type;
    }

    public Arg getDst() {
        return dst;
    }

    public Arg getSrc1() {
        return src1;
    }

    public Arg getSrc2() {
        return src2;
    }

    @Override
    public String toString() {
        return type +
                " " + dst +
                " " + src1 +
                " " + src2;
    }
}
