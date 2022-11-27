package compiler.mips;

import java.util.ArrayList;
import java.util.List;

public class Asm {
    private final List<String> lines;

    public Asm() {
        lines = new ArrayList<>();
    }

    public void addLine(String line) {
        lines.add(line);
    }

    public void addInstr(String instr, Object...nums) {
        StringBuilder sb = new StringBuilder();
        sb.append("\t");
        sb.append(instr);
        sb.append("\t");
        for (Object num : nums) {
            sb.append(num);
            sb.append("\t");
        }
        lines.add(sb.toString());
    }

    public void addEmptyLine() {
        lines.add("");
    }

    public void addLabel(String label) {
        lines.add(label + ":");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append('\n');
        }
        return sb.toString();
    }
}
