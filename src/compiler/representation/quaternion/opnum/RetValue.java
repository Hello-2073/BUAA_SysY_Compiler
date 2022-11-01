package compiler.representation.quaternion.opnum;

public class RetValue extends Arg {
    public RetValue() {
        super(OpnumType.RetValue);
    }

    @Override
    public String toString() {
        return "RETVAL";
    }
}
