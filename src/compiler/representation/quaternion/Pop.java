package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Pop extends Quaternion {
    public Pop(Operations operations, Arg dst) {
        super(operations, dst, null, null);
    }

    @Override
    public String toString() {
        return "pop " + getDst();
    }
}
