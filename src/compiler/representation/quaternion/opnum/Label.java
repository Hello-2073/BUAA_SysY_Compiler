package compiler.representation.quaternion.opnum;

import java.util.Objects;

public class Label extends Arg {
    private String name;

    public Label(String label) {
        super(OpnumType.Label);
        this.name = label;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return Objects.equals(name, label.name);
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
