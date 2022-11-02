package compiler.representation;

import compiler.error.Error;
import compiler.representation.module.Module;
import compiler.representation.quaternion.Binary;
import compiler.representation.quaternion.Call;
import compiler.representation.quaternion.Quaternion;
import compiler.representation.quaternion.Single;
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
        return symbolTable.insertVarSymbol(name, indices);
    }

    public static ConstEntry insertConstSymbol(String name, ArrayList<Integer> indices) throws Error {
        return symbolTable.insertConstSymbol(name, indices);
    }

    public static VarEntry insertFParamSymbol(String name, ArrayList<Integer> indices) throws Error {
        return symbolTable.insertFParamSymbol(name, indices);
    }

    public static FuncEntry insertFuncSymbolAndEnterScope(String name, String funcType) throws Error {
        FuncEntry funcEntry = symbolTable.insertFuncSymbolAndEnterScope(name, funcType);
        module.newFunc(funcEntry);
        return funcEntry;
    }

    public static Label addLabel() {
        Label tag = new Label(String.valueOf(tagCnt++));
        module.newBlock(tag);
        return tag;
    }

    public static void addLabel(Label tag) {
        tmpCnt = 0;
        module.newBlock(tag);
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
        } else if (src1.getType() != OpnumType.Imm) {
            Tmp tmp = newTmp();
            module.addQuaternion(new Binary(op, tmp, src1, src2));
            return tmp;
        } else if (src2.getType() != OpnumType.Imm) {
            Tmp tmp = newTmp();
            module.addQuaternion(new Binary(op, tmp, src2, src1));
            return tmp;
        } else {
            throw new RuntimeException();
        }
    }

    public static void addFunctionCall(Label label, List<Arg> args) {
        module.addQuaternion(new Call(label, args));
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


