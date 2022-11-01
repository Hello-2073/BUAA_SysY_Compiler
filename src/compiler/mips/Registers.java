package compiler.mips;

public class Registers {
    private int tmp = 0;
    private int save = 0;

    public Registers() {}

    public int allocTmp() {
        tmp = (tmp + 1) % 8;
        return tmp;
    }

    public int allocSave() {
        if (save < 8) {
            return save++;
        }
        return -1;
    }

    public void freeSave() {
        save = 0;
    }
}
