package compiler.mips;

public class Registers {
    private int tBitMap = 0;
    private int save = 0;

    public Registers() {}

    public int allocTmp() {
        for (int i = 0; i < 8; i++) {
            if (((tBitMap) >> i & 1) == 0) {
                tBitMap |= 1 << i;
                return i;
            }
        }
        return -1;
    }

    public void freeTmp(int i) {
        tBitMap &= ~(1 << i);
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
