package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

public class Ret extends Quaternion {
    private final Arg src;

    public Ret(Arg src) {
        super(Operations.RET, null, src, null);
        this.src = src;
    }

    public Arg getSrc() {
        return src;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ret ");
        if (src != null) {
            sb.append(src);
        }
        return sb.toString();
    }
}
