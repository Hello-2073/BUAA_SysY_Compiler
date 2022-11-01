package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Label;

public class Load extends Quaternion {
    private final Arg register;
    private final Arg symbol;
    private final Arg offset;

    public Load(Arg register, Arg symbol, Arg offset) {
        super(Operations.LOAD, register, symbol, offset);
        this.register = register;
        this.symbol = symbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return register + " = " + symbol + "[" + offset + "]";
    }
}
