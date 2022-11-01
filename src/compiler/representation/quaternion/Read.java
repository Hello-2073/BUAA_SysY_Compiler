package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Read extends Quaternion {
    private final Arg dst;

    public Read(Arg dst) {
        super(Operations.READ, dst, null, null);
        this.dst = dst;
    }

    @Override
    public Arg getDst() {
        return dst;
    }

    @Override
    public String toString() {
        return dst + " = GetInt()";
    }
}
