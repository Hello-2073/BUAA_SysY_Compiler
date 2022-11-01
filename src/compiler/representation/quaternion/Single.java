package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

import static compiler.representation.quaternion.Operations.SINGLE;

public class Single extends Quaternion {
    private final String op;
    private final Arg dst;
    private final Arg src;

    public Single(String type, Arg dst, Arg src) {
        super(SINGLE, dst, src, null);
        this.dst = dst;
        this.src = src;
        this.op = type;
    }

    public String getOp() {
        return op;
    }

    public Arg getDst() {
        return dst;
    }

    public Arg getSrc() {
        return src;
    }

    @Override
    public String toString() {
        return dst + " = " + op + " " + src;
    }
}
