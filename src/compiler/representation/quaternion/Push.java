package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Push extends Quaternion {
    private final int n;

    public Push(int n, Arg src1) {
        super(Operations.PUSH, null, src1, null);
        this.n = n;
    }

    public int getN() {
        return n;
    }

    @Override
    public String toString() {
        return "push " + getSrc1();
    }
}
