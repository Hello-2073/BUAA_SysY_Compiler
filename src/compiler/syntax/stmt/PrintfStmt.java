package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Write;
import compiler.representation.quaternion.opnum.Arg;
import compiler.representation.quaternion.opnum.Imm;
import compiler.representation.quaternion.opnum.Label;
import compiler.representation.quaternion.opnum.OpnumType;
import compiler.symbol.SymbolTable;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.exp.Exp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        int cnt = 0;
        String str = formatString.getContent();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < str.length() - 1; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                if (str.charAt(i + 1) != 'n') {
                    ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                    System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'\\'。");
                } else {
                    i++;
                    sb.append("\\n");
                }
            } else if (ch == '%') {
                if (str.charAt(i + 1) != 'd') {
                    ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                    System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'%'。");
                } else {
                    i++;
                    if (cnt < args.size()) {
                        args.get(cnt).translate(rets, params);
                        Arg dst = (Arg) rets.get("dst");
                        if (dst.getType() == OpnumType.Imm) {
                            sb.append(dst);
                        } else {
                            if (sb.length() > 0) {
                                Label label = Generator.addStr(sb.toString());
                                Generator.addQuaternion(new Write(label));
                                sb = new StringBuilder();
                            }
                            Generator.addQuaternion(new Write(dst));
                        }
                    }
                    cnt++;
                }
            } else if (ch != 32 && ch != 33 && (ch < 40 || ch > 126)) {
                ErrorRecorder.insert(new Error(formatString.getRow(), "a"));
                System.out.println("第 " + formatString.getRow() + " 行: FormatString 中存在非法字符'" + ch + "'。");
            } else {
                sb.append(ch);
            }
        }
        if (sb.length() > 0) {
            Label label = Generator.addStr(sb.toString());
            Generator.addQuaternion(new Write(label));
        }
        if (cnt != args.size()) {
            ErrorRecorder.insert(new Error(this.formatString.getRow(), "l"));
        }
        rets.replace("stmtType", "printfStmt");
    }
}
