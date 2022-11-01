package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Save extends Quaternion {
    private final Arg register;
    private final Arg symbol;
    private final Arg offset;

    public Save(Arg register, Arg symbol, Arg offset) {
        super(Operations.SAVE, register, symbol, offset);
        this.register = register;
        this.symbol = symbol;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return symbol.toString() + "["
                + offset.toString() + "] = "
                + register.toString();
    }
}
