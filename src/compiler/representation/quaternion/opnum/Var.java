package compiler.representation.quaternion.opnum;

import compiler.symbol.entry.VarEntry;

import java.util.Objects;

public class Var extends Arg {
    private final VarEntry varEntry;

    public Var(VarEntry varEntry) {
        super(OpnumType.Var);
        this.varEntry = varEntry;
    }

    public boolean isGlobal() {
        return varEntry.isGlobal();
    }

    public String getName() {
        return varEntry.getName();
    }

    public int getOffset() {
        return varEntry.getOffset();
    }

    public VarEntry getVarEntry() {
        return varEntry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var var = (Var) o;
        return Objects.equals(varEntry, var.varEntry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varEntry);
    }

    @Override
    public String toString() {
        return varEntry.getName();
    }
}
