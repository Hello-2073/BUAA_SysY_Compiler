package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;

import static compiler.representation.quaternion.Operations.JUMP;

public class Jump extends Quaternion {
    public Jump(Arg label) {
        super(JUMP, null, label, null);
        assert label instanceof Label;
    }

    @Override
    public String toString() {
        return "jump " + getSrc1();
    }
}
