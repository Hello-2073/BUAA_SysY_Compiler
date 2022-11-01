package compiler.mips;

import java.util.ArrayList;
import java.util.List;

public class Asm {
    private List<String> lines;

    public Asm() {
        lines = new ArrayList<>();
    }

    public void addLine(String line) {
        lines.add(line);
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
