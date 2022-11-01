package compiler.representation.quaternion.opnum;

import java.util.Objects;

public class Tmp extends Arg {
    private final String name;

    public Tmp(String name) {
        super(OpnumType.Tmp);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Tmp tmp = (Tmp) o;
        return Objects.equals(name, tmp.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
