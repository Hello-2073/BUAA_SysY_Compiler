package compiler.representation.quaternion.opnum;

import java.util.Objects;

public class Imm extends Arg {
    public static Imm calculate(String op, Imm src1, Imm src2) {
        switch (op) {
            case "+":
                return new Imm(src1.getValue() + src2.getValue());
            case "-":
                return new Imm(src1.getValue() - src2.getValue());
            case "*":
                return new Imm(src1.getValue() * src2.getValue());
            case "/":
                return new Imm(src1.getValue() / src2.getValue());
            case "%":
                return new Imm(src1.getValue() % src2.getValue());
            case "==":
                return new Imm(src1.getValue() == src2.getValue() ? 1 : 0);
            case "!=":
                return new Imm(src1.getValue() != src2.getValue() ? 1 : 0);
            case ">=":
                return new Imm(src1.getValue() >= src2.getValue() ? 1 : 0);
            case "<=":
                return new Imm(src1.getValue() <= src2.getValue() ? 1 : 0);
            case ">":
                return new Imm(src1.getValue() > src2.getValue() ? 1 : 0);
            case "<":
                return new Imm(src1.getValue() < src2.getValue() ? 1 : 0);
            case "bitand":
                return new Imm(src1.getValue() & src2.getValue());
            default:
                throw new RuntimeException("未知运算符" + op);
        }
    }

    public static Imm calculate(String op, Imm src) {
        switch (op) {
            case "+":
                return new Imm(+src.getValue());
            case "-":
                return new Imm(-src.getValue());
            case "!":
                return new Imm(src.getValue() == 0 ? 1 : 0);
            default:
                throw new RuntimeException("未知运算符");
        }
    }

    private final int value;

    public Imm(int value) {
        super(OpnumType.Imm);
        this.value = value;
    }

    public Imm(String value) {
        super(OpnumType.Imm);
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Imm imm = (Imm) o;
        return value == imm.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
