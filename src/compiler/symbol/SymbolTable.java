package compiler.symbol;

import compiler.error.Error;
import compiler.symbol.entry.*;
import compiler.symbol.scope.Scope;
import compiler.symbol.scope.ScopeType;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final Scope globalScope = new Scope(ScopeType.GLOBAL, null);
    private Scope currentScope = globalScope;
    private FuncEntry currentFunc = null;

    public void enterScope(ScopeType type) {
        currentScope = new Scope(type, currentScope);
    }

    public ScopeType getCurrentScopeType() {
        return currentScope.getType();
    }

    public ArrayList<VarEntry> getGlobalVars() {
        HashMap<String, Entry> globalEntrys = globalScope.getEntrys();
        ArrayList<VarEntry> varEntries = new ArrayList<>();
        for (String name : globalEntrys.keySet()) {
            SymbolType type = globalEntrys.get(name).getType();
            if (type == SymbolType.VAR || type == SymbolType.CONST) {
                varEntries.add((VarEntry) globalEntrys.get(name));
            }
        }
        return varEntries;
    }

    public void enterScope(FuncEntry funcEntry) {
        currentScope = new Scope(ScopeType.FUNC, currentScope);
        currentFunc = funcEntry;
    }

    public void exitScope() {
        if (currentScope.getType() == ScopeType.FUNC) {
            currentFunc = null;
        }
        currentScope = currentScope.getParent();
    }

    public void insert(Entry entry) throws Error {
        currentScope.insert(entry);
    }

    public Entry consult(String name) throws Error {
        return currentScope.consult(name);
    }

    public String getCurrentFuncType() {
        return currentFunc == null ? null : currentFunc.getFuncType();
    }

    public VarEntry insertVarSymbol(String name, ArrayList<Integer> indices) throws Error {
        VarEntry varEntry = new VarEntry(name, indices, currentFunc == null, false);
        currentScope.insert(varEntry);
        return varEntry;
    }

    public ConstEntry insertConstSymbol(String name, ArrayList<Integer> indices) throws Error {
        ConstEntry constEntry = new ConstEntry(name, indices, currentFunc == null);
        currentScope.insert(constEntry);
        return constEntry;
    }

    public VarEntry insertFParamSymbol(String name, ArrayList<Integer> indices) throws Error {
        assert currentFunc != null;
        VarEntry varEntry = new VarEntry(name, indices, false, true);
        currentScope.insert(varEntry);
        currentFunc.addFParam(varEntry);
        return varEntry;
    }

    public FuncEntry insertFuncSymbolAndEnterScope(String name, String funcType) throws Error {
        FuncEntry funcEntry = new FuncEntry(name, funcType);
        currentScope.insert(funcEntry);
        enterScope(funcEntry);
        return funcEntry;
    }
}
