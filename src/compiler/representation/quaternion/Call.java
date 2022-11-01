package compiler.representation.quaternion;

import compiler.representation.quaternion.opnum.Arg;

import java.util.List;

public class Call extends Quaternion {
    private final Arg label;
    private final List<Arg> args;

    public Call(Arg label, List<Arg> args) {
        super(Operations.CALL, null, label, null);
        this.label = label;
        this.args = args;
    }

    public Arg getLabel() {
        return label;
    }

    public List<Arg> getArgs() {
        return args;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(label);
        sb.append("(");
        if (args != null && args.size() > 0) {
            for (Arg arg : args) {
                sb.append(arg);
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(")");
        return sb.toString();
    }
}
