package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;

import static compiler.representation.quaternion.Operations.JUMP;

public class Jump extends Quaternion {
    private Label label;

    public Jump(Arg label) {
        super(JUMP, null, label, null);
        this.label = (Label) label;
    }

    @Override
    public String toString() {
        return "jump " + label;
    }
}
