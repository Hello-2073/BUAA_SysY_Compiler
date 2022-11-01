package compiler.syntax.decl.func;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.representation.Generator;
import compiler.syntax.Nonterminal;
import compiler.syntax.Syntax;
import compiler.syntax.Terminal;
import compiler.syntax.stmt.Block;
import compiler.syntax.SyntaxType;

import java.util.HashMap;

public class MainFuncDef extends Nonterminal {
    private Terminal maintk;
    private Block block;

    public MainFuncDef() {
        super(SyntaxType.MainFuncDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case MAINTK:
                maintk = (Terminal) child;
                break;
            case Block:
                block = (Block) child;
                break;
            default:
                break;
        }
    }

    @Override
    public void translate(HashMap<String, Object> rets, HashMap<String, Object> params) {
        try {
            Generator.insertFuncSymbolAndEnterScope("main", (String) "int");
            params.put("funcName", "main");
            block.translate(rets, params);
            Generator.exitScope();
        }  catch (Error e) {
            System.out.println("第" + maintk.getRow() + "行：重定义的符号" + "main");
            ErrorRecorder.insert(new Error(maintk.getRow(), "b"));
        }
    }
}
