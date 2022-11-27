package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Break extends Quaternion {
    private final String op;

    public Break(String op, Arg src1, Arg src2, Arg dst) {
        super(Operations.BREAK, dst, src1, src2);
        this.op = op;
    }

    public String getOp() {
        return op;
    }

    @Override
    public String toString() {
        return "if (" + getSrc1() + " " + op + " " + getSrc2() + ") goto " + getDst();
    }
}
