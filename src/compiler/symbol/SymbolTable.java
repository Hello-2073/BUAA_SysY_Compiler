package compiler.symbol;

import compiler.symbol.entry.Entry;
import compiler.symbol.entry.FuncEntry;
import compiler.type.ScopeType;

public class SymbolTable {
    private static Scope currentScope = new Scope(ScopeType.GLOBAL, null);
    private static FuncEntry currentFunc = null;

    public static void enterScope(ScopeType type) {
        currentScope = new Scope(type, currentScope);
    }

    public static void enterScope(FuncEntry funcEntry) {
        currentFunc = funcEntry;
        currentScope = new Scope(ScopeType.FUNC, currentScope);
    }

    public static void exitScope() {
        currentScope = currentScope.getParent();
    }

    public static void insert(Entry entry) throws Exception {
        // System.out.println("符号表登入" + entry.getName());
        currentScope.insert(entry);
    }

    public static Entry consult(String name) throws Exception {
        // System.out.println("符号表查询" + name);
        return currentScope.consult(name);
    }

    public static boolean hasInnerLoopScope() {
        return currentScope.getInnerLoopScope() != null;
    }

    public static boolean hasReturnValue() {
        if (currentFunc == null) {
            return false;
        }
        return "int".equals(currentFunc.getFuncType());
    }

    public static ScopeType getCurrentScopeType() {
        return currentScope.getType();
    }
}
