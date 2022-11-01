package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Binary extends Quaternion {
    private final String op;
    private final Arg dst;
    private final Arg src1;
    private final Arg src2;

    public Binary(String type, Arg dst, Arg src1, Arg src2) {
        super(Operations.BINARY, dst, src1, src2);
        this.dst = dst;
        this.src1 = src1;
        this.op = type;
        this.src2 = src2;
    }

    public String getOp() {
        return op;
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
        return dst.toString() + " = "
                + src1.toString() + " "
                + op + " " + src2.toString();
    }
}
