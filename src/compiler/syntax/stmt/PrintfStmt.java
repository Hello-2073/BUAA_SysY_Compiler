package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.Exp;

import java.util.ArrayList;

public class PrintfStmt extends Stmt {
    private Terminal formatString;
    private final ArrayList<Exp> args = new ArrayList<>();

    @Override
    public void addChild(Syntax syntax) {
        super.addChild(syntax);
        switch (syntax.getType()) {
            case STRCON:
                formatString = (Terminal) syntax;
                break;
            case Exp:
                args.add((Exp) syntax);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate() {
        int cnt = 0;
        String str = formatString.getContent();
        for (int i = 1; i < str.length() - 1; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                if (str.charAt(i + 1) != 'n') {
                    ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                    System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'\\'。");
                    return;
                } else {
                    i++;
                }
            } else if (ch == '%') {
                if (str.charAt(i + 1) != 'd') {
                    ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                    System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'%'。");
                    return;
                } else {
                    i++;
                    cnt++;
                }
            } else if (ch != 32 && ch != 33 && (ch < 40 || ch > 126)) {
                ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'" + ch + "'。");
                return;
            }
        }
        if (cnt != args.size()) {
            ErrorRecorder.insert(new Error(this.formatString.getRow(), "l"));
        }
    }
}
