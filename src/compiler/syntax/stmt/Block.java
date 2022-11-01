package compiler.syntax.stmt;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.representation.quaternion.Read;
import compiler.representation.quaternion.Ret;
import compiler.symbol.scope.ScopeType;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.SyntaxType;
import compiler.syntax.Terminal;

import java.util.ArrayList;
import java.util.HashMap;

public class Block extends Nonterminal {
    private final ArrayList<BlockItem> items = new ArrayList<>();
    private Terminal rbrace;

    public Block() {
        super(SyntaxType.Block);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case RBRACE:
                rbrace = (Terminal) child;
                break;
            case BlockItem:
                items.add((BlockItem) child);
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        rets.put("stmtType", null);
        for (BlockItem item : items) {
            item.translate(rets, params);
        }
        if (Generator.getCurrentScopeType() == ScopeType.FUNC
                &&!"returnStmt".equals(rets.get("stmtType"))) {
            if ("int".equals(Generator.getCurrentFuncType())) {
                System.out.println("第 " + rbrace.getRow() + " 行：函数无返回值。");
                ErrorRecorder.insert(new Error(rbrace.getRow(), "g"));

            }
            Generator.addQuaternion(new Ret(null));
        }
    }
}
