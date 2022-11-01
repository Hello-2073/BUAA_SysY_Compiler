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

public class FuncDef extends Nonterminal {
    private FuncType funcType;
    private Terminal ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef() {
        super(SyntaxType.FuncDef);
    }

    @Override
    public void addChild(Syntax child) {
        super.addChild(child);
        switch (child.getType()) {
            case FuncType:
                funcType = (FuncType) child;
                break;
            case IDENFR:
                ident = (Terminal) child;
                break;
            case FuncFParams:
                funcFParams = (FuncFParams) child;
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
        funcType.translate(rets, params);
        try {
            Generator.insertFuncSymbolAndEnterScope(ident.getContent(), (String) rets.get("funcType"));
            params.put("funcName", ident.getContent());
            if (funcFParams != null) {
                funcFParams.translate(rets, params);
            }
            block.translate(rets, params);
            Generator.exitScope();
        }  catch (Error e) {
            System.out.println("第" + ident.getRow() + "行：重复定义的符号" + ident.getContent());
            ErrorRecorder.insert(new Error(ident.getRow(), "b"));
        }
    }
}
