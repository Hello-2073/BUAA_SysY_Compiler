package compiler.mips;

import compiler.representation.module.BasicBlock;
import compiler.representation.module.Function;
import compiler.representation.module.Module;
import compiler.representation.quaternion.*;
import compiler.representation.quaternion.opnum.*;
import compiler.symbol.entry.VarEntry;

import java.util.*;

public class Translator {
    private static Module module;
    private static Function function;

    private static Asm asm;

    private static int stackSpace;
    private static int localVarBase;
    private static int tmpRegBase;
    private static int saveRegBase;

    private static HashMap<Arg, Integer> frameMap;
    private static HashMap<Var, String> sRegMap;
    private static HashMap<Tmp, String> tRegMap;

    private static HashMap<String, Integer> regSavedByCalled;
    private static HashMap<String, Integer> regSavedByCaller;

    public static Asm getAsm() {
        return asm;
    }

    public static void reset(Module module) {
        Translator.module = module;
        asm = new Asm();
    }

    private static void loadVar(Var var, String reg) {
        if (var.isGlobal()) {
            if (var.getDim() > 0) {
                asm.addInstr("la", reg, var.getName());
            } else {
                asm.addInstr("lw", reg, var.getName() + "($zero)");
            }
        } else {
            if (var.getDim() > 0 && !var.isFParam()) {
                asm.addInstr("addiu", reg, "$fp", frameMap.get(var));
            } else {
                asm.addInstr("lw", reg, frameMap.get(var) + "($fp)");
            }
        }
    }

    private static void saveVar(Arg arg, String reg) {
        if (arg instanceof Var && ((Var) arg).isGlobal()) {
            asm.addInstr("sw", reg, arg + "($zero)");
        } else {
            asm.addInstr("sw", reg, frameMap.get(arg)+ "($fp)");
        }
    }

    public static String getDstReg(Arg arg) {
        switch (arg.getType()) {
            case Tmp:
                if (tRegMap.containsKey(arg)) {
                    return tRegMap.get(arg);
                }
                return "$v0";
            case Var:
                if (sRegMap.containsKey(arg)) {
                    return sRegMap.get(arg);
                }
                return "$v0";
            default:
                throw new RuntimeException();
        }
    }

    public static String getSrcReg1(Arg arg) {
        switch (arg.getType()) {
            case RetValue:
                return "$v0";
            case Imm:
                asm.addInstr("li", "$v0", arg);
                return "$v0";
            case Tmp:
                if (tRegMap.containsKey(arg)) {
                    return tRegMap.get(arg);
                }
                asm.addInstr("lw", "$v0", frameMap.get(arg) + "($fp)");
                return "$v0";
            case Var:
                if (sRegMap.containsKey((Var) arg)) {
                    return sRegMap.get(arg);
                }
                loadVar((Var) arg, "$v0");
                return "$v0";
            default:
                throw new RuntimeException(arg.getType() + "!!");
        }
    }

    private static String getSrcReg2(Arg arg) {
        switch (arg.getType()) {
            case RetValue:
                return "$v1";
            case Imm:
                asm.addInstr("li", "$v1", arg);
                return "$v1";
            case Tmp:
                if (tRegMap.containsKey(arg)) {
                    return tRegMap.get(arg);
                }
                asm.addInstr("lw", "$v1", frameMap.get(arg) + "($fp)");
                return "$v1";
            case Var:
                if (sRegMap.containsKey((Var) arg)) {
                    return sRegMap.get(arg);
                }
                loadVar((Var) arg, "$v1");
                return "$v1";
            default:
                return null;
        }
    }

    public static void translate() {
        translate(module);
    }

    private static void dataSeg(Module module) {
        asm.addLine(".data");
        HashMap<Label, String> strs = module.getConstStr();
        for (Label label : strs.keySet()) {
            asm.addLine(label + ":\t.asciiz\t\"" + strs.get(label) + "\"");
        }
        asm.addLine(".align 2");
        for (VarEntry varEntry : module.globalVars()) {
            StringBuilder sb = new StringBuilder();
            sb.append(varEntry.getName());
            sb.append(": ");
            if (varEntry.getInitVals().size() > 0) {
                sb.append(".word ");
                for (int val : varEntry.getInitVals()) {
                    sb.append(val);
                    sb.append(" ");
                }
            }
            int left = varEntry.size() - 4 * varEntry.getInitVals().size();
            if (left > 0) {
                sb.append(".space ");
                sb.append(left);
            }
            asm.addLine(sb.toString());
        }
    }

    private static void translate(Module module) {
        dataSeg(module);
        asm.addEmptyLine();
        asm.addLine(".text");
        asm.addInstr("move", "$fp", "$sp");
        asm.addInstr("jal", "main");
        asm.addInstr("nop");
        asm.addInstr("li", "$v0", "10");
        asm.addInstr("syscall");
        asm.addEmptyLine();
        for (Function function : module.getFunctions()) {
            Translator.function = function;
            translate(function);
            asm.addEmptyLine();
        }
    }

    private static void preview(Function function) {
        int args = 4 * function.getMaxArgNum(), vars = 0, tmpReg = 0, savedReg = 0;
        sRegMap = new HashMap<>();
        tRegMap = new HashMap<>();
        frameMap = new HashMap<>();
        regSavedByCalled = new HashMap<>();
        localVarBase = args;
        for (Var localVar : function.getLocalVars()) {
            int i = function.getLocalVars().indexOf(localVar);
            if (i < 8 && (localVar.isFParam() || localVar.getDim() == 0)) {
                sRegMap.put(localVar, "$s" + i);
                regSavedByCalled.put("$s" + i, 0);
            }
            frameMap.put(localVar, localVarBase + vars);
            vars += localVar.size();
        }
        tmpRegBase = localVarBase + vars;
        List<BasicBlock> basicBlocks = function.getBasicBlocks();
        for (BasicBlock basicBlock : basicBlocks) {
            List<Set<Tmp>> tmpGroups = basicBlock.getTmpGroups();
            tmpReg = Math.max(tmpReg, 4 * tmpGroups.size());
            for (int i = 0; i < tmpGroups.size(); i++) {
                for (Tmp tmp : tmpGroups.get(i)) {
                    if (i < 8) {
                        tRegMap.put(tmp, "$t" + i);
                    }
                    frameMap.put(tmp, tmpRegBase + 4 * i);
                }
            }
        }
        saveRegBase = tmpRegBase + tmpReg;
        for (String reg : regSavedByCalled.keySet()) {
            regSavedByCalled.replace(reg, saveRegBase + savedReg);
            savedReg += 4;
        }
        savedReg += function.isLeaf() ? 4 : 8; // $fp, $ra
        stackSpace = saveRegBase + savedReg;
        asm.addLabel(function.getName());
        asm.addInstr("# frame: ","saved:" + savedReg + ", tmp" + tmpReg + ", vars:" + vars + ", args:" + args);
        asm.addInstr("addi", "$sp", "$sp", -stackSpace);
        if (function.isLeaf()) {
            asm.addInstr("sw", "$fp", (stackSpace - 4) + "($sp)");
        } else {
            asm.addInstr("sw", "$ra", (stackSpace - 4) + "($sp)");
            asm.addInstr("sw", "$fp", (stackSpace - 8) + "($sp)");
        }
        for (String reg : regSavedByCalled.keySet()) {
            asm.addInstr("sw", reg, regSavedByCalled.get(reg) + "($sp)");
        }
        asm.addInstr("move", "$fp",  "$sp");
    }

    private static void loadArgs(Function function) {
        for (int i = 0; i < function.getFParaNum(); i++) {
            Var fParam = function.getFParam(i);
            if (i <= 3) {
                if (sRegMap.containsKey(fParam)) {
                    asm.addInstr("move",sRegMap.get(fParam), "$a" + i);
                } else {
                    asm.addInstr("sw","$a" + i, frameMap.get(fParam) + "($fp)");
                }
            } else {
                if (sRegMap.containsKey(fParam)) {
                    asm.addInstr("lw", sRegMap.get(fParam), (stackSpace + 4 * i) + "($sp)");
                } else {
                    asm.addInstr("lw","$v0", (stackSpace + 4 * i) + "($sp)");
                    asm.addInstr("sw","$v0", frameMap.get(fParam) + "($fp)");
                }
            }
        }
    }

    private static void translate(Function function) {
        preview(function);
        loadArgs(function);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            translate(basicBlock);
        }
    }

    private static void translate(BasicBlock basicBlock) {
        regSavedByCaller = new HashMap<>();
        for (int i = 0; i < basicBlock.getTmpGroups().size() && i < 8; i++) {
            regSavedByCaller.put("$t" + i, tmpRegBase + 4 * i);
        }
        Label blockName = basicBlock.getBlockName();
        if (blockName != null) {
            asm.addLine(basicBlock.getBlockName() + ":");
        }
        for (Quaternion quaternion : basicBlock.getQuaternions()) {
            translate(quaternion);
        }
    }

    private static void translate(Quaternion quaternion) {
        switch (quaternion.getType()) {
            case READ:
                translate((Read) quaternion);
                break;
            case WRITE:
                translate((Write) quaternion);
                break;
            case SINGLE:
                translate((Single) quaternion);
                break;
            case BINARY:
                translate((Binary) quaternion);
                break;
            case PUSH:
                translate((Push) quaternion);
                break;
            case CALL:
                translate((Call) quaternion);
                break;
            case RET:
                translate((Ret) quaternion);
                break;
            case SAVE:
                translate((Save) quaternion);
                break;
            case LOAD:
                translate((Load) quaternion);
                break;
            case JUMP:
                translate((Jump) quaternion);
                break;
            case BREAK:
                translate((Break) quaternion);
                break;
            default:
                throw new RuntimeException(quaternion + " is not translated");
        }
    }

    private static void translate(Read read) {
        asm.addInstr("li","$v0", "5");
        asm.addInstr("syscall");
        Arg dst = read.getDst();
        String dstReg = getDstReg(dst);
        if (!dstReg.equals("$v0")) {
            asm.addInstr("move", dstReg, "$v0");
        } else {
            saveVar(dst, "$v0");
        }
    }

    private static void translate(Write write) {
        Arg src = write.getSrc();
        if (src.getType() == OpnumType.Label) {
            asm.addInstr("la",   "$a0", src);
            asm.addInstr("li", "$v0", 4);
        } else {
            String reg = getSrcReg1(src);
            asm.addInstr("move", "$a0", reg);
            asm.addInstr("li", "$v0", 1);
        }
        asm.addInstr("syscall");
    }

    private static void translate(Single single) {
        String op = single.getOp();
        Arg src = single.getSrc1();
        Arg dst = single.getDst();
        String srcReg = getSrcReg1(src);
        String dstReg = getDstReg(dst);
        String instr = op.equals("+") ? "addu":
                       op.equals("-") ? "subu":
                       op.equals("!") ? "seq": null;
        assert instr != null;
        asm.addInstr(instr, dstReg, "$zero", srcReg);
        if (dstReg.equals("$v0")) {
            saveVar(dst, "$v0");
        }
    }

    private static void translate(Binary binary) {
        String op = binary.getOp();
        Arg src1 = binary.getSrc1();
        Arg src2 = binary.getSrc2();
        Arg dst = binary.getDst();
        String srcReg1 = getSrcReg1(src1);
        String srcReg2 = getSrcReg2(src2);
        String dstReg = getDstReg(dst);
        if (op.equals("%") || op.equals("/")) {
            String instr = op.equals("%") ? "mfhi" : "mflo";
            asm.addInstr("div", srcReg1, srcReg2);
            asm.addInstr(instr, dstReg);
        } else  {
            String instr = op.equals("+") ? "addu":
                        op.equals("-") ? "subu":
                        op.equals("*") ? "mulu":
                        op.equals("==") ? "seq":
                        op.equals("!=") ? "sne":
                        op.equals(">=") ? "sge":
                        op.equals("<=") ? "sle":
                        op.equals(">") ? "sgt":
                        op.equals("<") ? "slt" :
                                op.equals("bitand") ? "and" : null;
            assert instr != null;
            asm.addInstr(instr, dstReg, srcReg1, srcReg2);
        }
        if (dstReg.equals("$v0")) {
            saveVar(dst, "$v0");
        }
    }

    private static void translate(Push push) {
        int i = push.getN();
        Arg arg = push.getSrc1();
        if (arg instanceof Imm) {
            if (i < 4) {
                asm.addInstr("li", "$a" + i, arg);
            } else {
                asm.addInstr("li", "$v0", arg);
                asm.addInstr("sw", "$v0", (4 * i) + "($sp)");
            }
        } else {
            String argReg = getSrcReg1(arg);
            if (i < 4) {
                asm.addInstr("move", "$a" + i, argReg);
            } else {
                asm.addInstr("sw", argReg,  (4 * i) + "($sp)");
            }
        }
    }

    private static void translate(Call call) {
        for (String tmpReg : regSavedByCaller.keySet()) {
            asm.addInstr("sw", tmpReg, regSavedByCaller.get(tmpReg) + "($fp)");
        }
        asm.addInstr("jal", call.getLabel());
        asm.addInstr("nop");
        for (String tmpReg : regSavedByCaller.keySet()) {
            asm.addInstr("lw", tmpReg, regSavedByCaller.get(tmpReg) + "($fp)");
        }
    }

    private static void translate(Ret ret) {
        Arg retValue = ret.getSrc();
        if (retValue instanceof Imm) {
            asm.addInstr("li", "$v0", ((Imm) retValue).getValue());
        } else if (retValue != null) {
            String reg = getSrcReg1(retValue);
            if (!reg.equals("$v0")) {
                asm.addInstr("move","$v0", reg);
            }
        }
        asm.addInstr("addiu","$sp", "$fp", stackSpace);
        for (String reg : regSavedByCalled.keySet()) {
            asm.addInstr("lw", reg, regSavedByCalled.get(reg) + "($fp)");
        }
        if (function.isLeaf()) {
            asm.addInstr("lw","$fp", (stackSpace - 4) + "($fp)");
        } else {
            asm.addInstr("lw", "$ra", (stackSpace - 4) + "($fp)");
            asm.addInstr("lw","$fp", (stackSpace - 8) + "($fp)");
        }
        asm.addInstr("jr","$ra");
        asm.addInstr("nop");
    }

    public static void translate(Save save) {
        /* dst[src1] = src2 */
        Arg src1 = save.getSrc1();
        Arg src2 = save.getSrc2();
        Var dst = (Var) save.getDst();
        if (src1 instanceof Imm) {
            asm.addInstr("li", "$v0", 4 * ((Imm) src1).getValue());
        } else {
            asm.addInstr("sll", "$v0", getSrcReg1(src1), 2);
        }
        if (dst.isGlobal()) {
            asm.addInstr("sw", getSrcReg2(src2), dst + "($v0)");
        } else if (dst.isFParam()) {
            asm.addInstr("addu", "$v0", "$v0", getSrcReg2(dst));
            asm.addInstr("sw", getSrcReg2(src2), "0($v0)");
        } else {
            asm.addInstr("addu", "$v0", "$v0", "$fp");
            asm.addInstr("sw", getSrcReg2(src2), frameMap.get(dst) + "($v0)");
        }
    }

    public static void translate(Load load) {
        /* dst = src1[src2]; */
        Var src1 = (Var) load.getSrc1();
        Arg src2 = load.getSrc2();
        Arg dst = load.getDst();
        String dstReg = getDstReg(dst);
        if (src2 instanceof Imm) {
            asm.addInstr("li", "$v0", 4 * ((Imm) src2).getValue());
        } else {
            asm.addInstr("sll", "$v0", getSrcReg2(src2), 2);
        }
        if (src1.isGlobal()) {
            asm.addInstr("lw", dstReg, src1 + "($v0)");
        } else if (src1.isFParam()) {
            String srcReg1 = getSrcReg1(src1);
            asm.addInstr("addu", "$v0", srcReg1, "$v0");
            asm.addInstr("lw", dstReg, "0($v0)");
        }else {
            asm.addInstr("addu", "$v0", "$v0", "$fp");
            asm.addInstr("lw", dstReg, frameMap.get(src1) + "($v0)");
        }
        if (dstReg.equals("$v0")) {
            saveVar(dst, "$v0");
        }
    }

    public static void translate(Jump jump) {
        asm.addInstr("j", jump.getSrc1());
        asm.addInstr("nop");
    }

    public static void translate(Break bk) {
        String op = bk.getOp();
        Arg src1 = bk.getSrc1();
        Arg src2 = bk.getSrc2();
        Arg dst = bk.getDst();
        String srcReg1 = getSrcReg1(src1);
        String srcReg2 = getSrcReg2(src2);
        String instr = op.equals("==") ? "beq":
                       op.equals("!=") ? "bne":
                       op.equals("<")  ? "blz":
                       op.equals(">")  ? "bgz":
                       op.equals("<=") ? "ble":
                       op.equals(">=") ? "bge": null;
        asm.addInstr(instr, srcReg1, srcReg2, dst.toString());
        asm.addInstr("nop");
    }
}
