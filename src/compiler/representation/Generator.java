package compiler.representation;

import compiler.error.Error;
import compiler.representation.module.Module;
import compiler.representation.quaternion.*;
import compiler.representation.quaternion.opnum.*;
import compiler.symbol.SymbolTable;
import compiler.symbol.entry.ConstEntry;
import compiler.symbol.entry.Entry;
import compiler.symbol.entry.FuncEntry;
import compiler.symbol.entry.VarEntry;
import compiler.symbol.scope.Scope;
import compiler.symbol.scope.ScopeType;

import java.util.ArrayList;
import java.util.List;

public class Generator {
    private static SymbolTable symbolTable;
    private static Module module;

    private static int tmpCnt = 0;
    private static int tagCnt = 0;
    private static int strCnt = 0;

    private Generator() {
    }

    public static void reset() {
        symbolTable = new SymbolTable();
        module = new Module(symbolTable);
    }

    public static Label addStr(String str) {
        Label label = new Label("str_" + strCnt++);
        module.addStr(label, str);
        return label;
    }

    public static VarEntry insertVarSymbol(String name, ArrayList<Integer> indices) throws Error {
        VarEntry varEntry = symbolTable.insertVarSymbol(name, indices);
        module.addLocalVar(new Var(varEntry));
        return varEntry;
    }

    public static ConstEntry insertConstSymbol(String name, ArrayList<Integer> indices) throws Error {
        ConstEntry constEntry = symbolTable.insertConstSymbol(name, indices);
        module.addLocalVar(new Var(constEntry));
        return constEntry;
    }

    public static VarEntry insertFParamSymbol(String name, ArrayList<Integer> indices) throws Error {
        VarEntry varEntry = symbolTable.insertFParamSymbol(name, indices);
        module.addLocalVar(new Var(varEntry));
        return varEntry;
    }

    public static FuncEntry insertFuncSymbolAndEnterScope(String name, String funcType) throws Error {
        FuncEntry funcEntry = symbolTable.insertFuncSymbolAndEnterScope(name, funcType);
        module.newFunc(funcEntry);
        return funcEntry;
    }

    public static Label allocLabel() {
        return new Label("$L" + String.valueOf(tagCnt++));
    }

    public static void addLabel(Label label) {
        module.newBlock(label);
    }

    public static Tmp newTmp() {
        return new Tmp("t" + tmpCnt++);
    }

    public static Arg addSingle(String op, Arg src) {
        if (src.getType() == OpnumType.Imm) {
            return Imm.calculate(op, (Imm) src);
        } else if (src.getType() == OpnumType.Var
                || src.getType() == OpnumType.Tmp
                || src.getType() == OpnumType.RetValue) {
            Tmp tmp = newTmp();
            module.addQuaternion(new Single(op, tmp, src));
            return tmp;
        } else {
            throw new RuntimeException();
        }
    }

    public static Arg addBinary(String op, Arg src1, Arg src2) {
        if (src1.getType() == OpnumType.Imm && src2.getType() == OpnumType.Imm ) {
            return Imm.calculate(op, (Imm) src1, (Imm) src2);
        } else {
            Tmp tmp = newTmp();
            module.addQuaternion(new Binary(op, tmp, src1, src2));
            return tmp;
        }
    }

    public static void addFunctionCall(Label label, List<Arg> args) {
        module.addQuaternion(new Call(label, args));
    }

    public static void addBreakIf(String op, Arg src1, Arg src2, Label label) {
        if (src1.getType() == OpnumType.Imm && src2.getType() == OpnumType.Imm ) {
             Imm res = Imm.calculate(op, (Imm) src1, (Imm) src2);
             if (res.getValue() != 0) {
                 module.addQuaternion(new Jump(label));
             }
        } else {
            module.addQuaternion(new Break(op, src1, src2, label));
        }
    }

    public static void addQuaternion(Quaternion quaternion) {
        module.addQuaternion(quaternion);
    }

    public static Entry consultSymbol(String name) throws Error {
        return symbolTable.consult(name);
    }

    public static void enterScope(ScopeType type) {
        symbolTable.enterScope(type);
    }

    public static void exitScope() {
        symbolTable.exitScope();
    }

    public static String getCurrentFuncType() {
        return symbolTable.getCurrentFuncType();
    }

    public static Module getModule() {
        return module;
    }

    public static ScopeType getCurrentScopeType() {
        return symbolTable.getCurrentScopeType();
    }
}


