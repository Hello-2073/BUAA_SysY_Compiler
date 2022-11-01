package compiler.representation.quaternion.opnum;

import compiler.representation.quaternion.Operations;

public class Arg {
    private final OpnumType type;

    public Arg(OpnumType type) {
        this.type = type;
    }

    public OpnumType getType() {
        return type;
    }
}
