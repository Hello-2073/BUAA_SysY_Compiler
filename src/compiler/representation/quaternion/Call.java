package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

import java.util.ArrayList;
import java.util.List;

public class Call extends Quaternion {
    private final Arg label;

    public Call(Arg label) {
        super(Operations.CALL, null, label, null);
        this.label = label;
    }

    public Arg getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "call " + label;
    }
}
