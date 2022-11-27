package compiler.mips;

import compiler.representation.module.BasicBlock;
import compiler.representation.module.Function;
import compiler.representation.module.Module;
import compiler.representation.quaternion.*;
import compiler.representation.quaternion.opnum.*;
import compiler.symbol.entry.VarEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Translator {
    private static Module module;
    private static Function function;

    private static Asm asm;

    private static Registers registers;

    private static int stackSpace;
    private static HashMap<Var, Integer> stackMap;
    private static HashMap<Var, String> sRegMap;
    private static HashMap<Tmp, String> tRegMap;

    public static Asm getAsm() {
        return asm;
    }

    public static void reset(Module module) {
        Translator.module = module;
        asm = new Asm();
        registers = new Registers();
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
                asm.addInstr("addiu", reg, "$fp", stackMap.get(var));
            } else {
                asm.addInstr("lw", reg, stackMap.get(var) + "($fp)");
            }
        }
    }

    private static void saveVar(Var var, String reg) {
        if (var.isGlobal()) {
            asm.addInstr("sw", reg, var.getName() + "($zero)");
        } else {
            asm.addInstr("sw", reg, stackMap.get(var)+ "($fp)");
        }
    }

    public static String getDstReg(Arg arg) {
        switch (arg.getType()) {
            case Tmp:
                String reg = "$t" + registers.allocTmp();
                tRegMap.put((Tmp) arg, reg);
                return reg;
            case Var:
                return sRegMap.get(arg);
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
                String reg = tRegMap.get(arg);
                tRegMap.remove(arg);
                registers.freeTmp(reg.charAt(2) - '0');
                return reg;
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
                String reg = tRegMap.get(arg);
                tRegMap.remove(arg);
                registers.freeTmp(reg.charAt(2) - '0');
                return reg;
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

    private static void translate(Module module) {
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
        registers.freeAllSave();
        sRegMap = new HashMap<>();
        stackMap = new HashMap<>();
        stackSpace = 0;
        for (Var localVar : function.getLocalVars()) {
            int i = registers.allocSave();
            if (i != -1 && (localVar.isFParam() || localVar.getDim() == 0)) {
                sRegMap.put(localVar, "$s" + i);
            }
            stackMap.put(localVar, stackSpace);
            stackSpace += localVar.size();
        }
        stackSpace += 8; // $fp, $ra
        System.out.println(function.getName() + ":" + stackMap + " in " + stackSpace);
        System.out.println(sRegMap);
        asm.addLabel(function.getName());
        asm.addInstr("addi", "$sp", "$sp", -stackSpace);
        asm.addInstr("sw", "$fp", (stackSpace - 8) + "($sp)");
        asm.addInstr("sw", "$ra", (stackSpace - 4) + "($sp)");
        asm.addInstr("move", "$fp",  "$sp");
        for (int i = 0; i < function.getFParaNum(); i++) {
            Var fParam = function.getFParam(i);
            if (i <= 3) {
                if (sRegMap.containsKey(fParam)) {
                    asm.addInstr("move",sRegMap.get(fParam), "$a" + i);
                } else {
                    asm.addInstr("sw","$a" + i, (4 * i) + "($fp)");
                }
            } else {
                if (sRegMap.containsKey(fParam)) {
                    asm.addInstr("lw", sRegMap.get(fParam), (stackSpace + 4 * (i - 4)) + "($sp)");
                } else {
                    asm.addInstr("lw","$v0", (stackSpace + 4 * (i - 4)) + "($sp)");
                    asm.addInstr("sw","$v0", (4 * i) + "($fp)");
                }
            }
        }
    }

    private static void translate(Function function) {
        preview(function);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            translate(basicBlock);
        }
    }

    private static void translate(BasicBlock basicBlock) {
        registers.freeAllTmp();
        tRegMap = new HashMap<>();
        Label blockName = basicBlock.getLabel();
        if (blockName != null) {
            asm.addLine(basicBlock.getLabel() + ":");
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
        if (dstReg != null) {
            asm.addInstr("move", dstReg, "$v0");
        } else {
            saveVar((Var) dst, "$v0");
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
        String srcReg = src instanceof Imm ? src.toString() :
                                             getSrcReg1(src);
        String dstReg = getDstReg(dst);
        String instr = op.equals("+") ? "addu":
                       op.equals("-") ? "subu":
                       op.equals("!") ? "seq": null;
        assert instr != null;
        if (dstReg != null) {
            asm.addInstr(instr, dstReg, "$zero", srcReg);
        } else {
            asm.addInstr(instr, "$v0", "$zero", srcReg);
            saveVar((Var) dst, "$v0");
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
        if (op.equals("%")) {
            asm.addInstr("div", srcReg1, srcReg2);
            if (dstReg != null) {
                asm.addInstr("mfhi", dstReg);
            } else {
                asm.addInstr("mfhi", "$v0");
                saveVar((Var) dst, "$v0");
            }
        } else {
            String instr = op.equals("+") ? "addu":
                        op.equals("-") ? "subu":
                        op.equals("*") ? "mulu":
                        op.equals("/") ? "div":
                        op.equals("==") ? "seq":
                        op.equals("!=") ? "sne":
                        op.equals(">=") ? "sge":
                        op.equals("<=") ? "sle":
                        op.equals(">") ? "sgt":
                        op.equals("<") ? "slt" : null;
            assert instr != null;
            if (dstReg != null) {
                asm.addInstr(instr, dstReg, srcReg1, srcReg2);
            } else {
                asm.addInstr(instr, "$v0", srcReg1, srcReg2);
                saveVar((Var) dst, "$v0");
            }
        }
    }

    private static void translate(Call call) {
        for (Var arg : sRegMap.keySet()) {
            String reg = sRegMap.get(arg);
            asm.addInstr("sw", reg, stackMap.get(arg) + "($fp)");
        }
        ArrayList<String> tRegs = new ArrayList<>();
        for (Arg arg : tRegMap.keySet()) {
            tRegs.add(tRegMap.get(arg));
        }
        if (tRegs.size() > 0) {
            asm.addInstr("addiu", "$sp", "$sp", -4 * tRegs.size());
        }
        for (int i =0; i < tRegs.size(); i++) {
            asm.addInstr("sw", tRegs.get(i), (4 * i) + "($sp)");
        }
        List<Arg> args = call.getArgs();
        if (args.size() > 4) {
            asm.addInstr("addiu","$sp", "$sp", -4 * (args.size() - 4));
        }
        for (int i = 0; i < args.size(); i++) {
            Arg arg = args.get(i);
            if (arg.getType() == OpnumType.Imm) {
                if (i < 4) {
                    asm.addInstr("li", "$a" + i, arg);
                } else {
                    asm.addInstr("li", "$v0", arg);
                    asm.addInstr("sw", "$v0", (4 * (i - 4)) + "($sp)");
                }
            } else {
                String argReg = getSrcReg1(args.get(i));
                if (i < 4) {
                    asm.addInstr("move", "$a" + i, argReg);
                } else {
                    asm.addInstr("sw", argReg,  (4 * (i - 4)) + "($sp)");
                }
            }
        }
        asm.addInstr("jal", call.getLabel());
        asm.addInstr("nop");
        if (args.size() > 4) {
            asm.addInstr("addiu", "$sp", "$sp", 4 * (args.size() - 4));
        }
        for (int i =0; i < tRegs.size(); i++) {
            asm.addInstr("lw", tRegs.get(i), (4 * i) + "($sp)");
        }
        if (tRegs.size() > 0) {
            asm.addInstr("addiu", "$sp", "$sp", 4 * tRegs.size());
        }
        for (Var arg : sRegMap.keySet()) {
            String reg = sRegMap.get(arg);
            asm.addInstr("lw", reg, stackMap.get(arg) + "($fp)");
        }
    }

    private static void translate(Ret ret) {
        Arg retValue = ret.getSrc();
        if (retValue != null) {
            if (retValue.getType() == OpnumType.Imm) {
                asm.addInstr("li", "$v0", ((Imm) retValue).getValue());
            } else {
                String reg = getSrcReg1(retValue);
                if (!reg.equals("$v0")) {
                    asm.addInstr("move","$v0", reg);
                }
            }
        }
        asm.addInstr("lw", "$ra", (stackSpace - 4) + "($fp)");
        asm.addInstr("addiu","$sp", "$fp", stackSpace);
        asm.addInstr("lw","$fp", (stackSpace - 8) + "($fp)");
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
            asm.addInstr("sw", getSrcReg2(src2), stackMap.get(dst) + "($v0)");
        }
    }

    public static void translate(Load load) {
        /* dst = src1[src2]; */
        Var src1 = (Var) load.getSrc1();
        Arg src2 = load.getSrc2();
        Arg dst = load.getDst();
        String srcReg2 = getSrcReg2(src2);
        String dstReg = getDstReg(dst);
        if (src2 instanceof Imm) {
            asm.addInstr("li", "$v1", src2);
            srcReg2 = "$v1";
        }
        if (src1.isGlobal()) {
            asm.addInstr("sll", "$v0", srcReg2, 2);
            asm.addInstr("lw", dstReg, src1+"($v0)");
        } else if (src1.isFParam()) {
            asm.addInstr("sll", "$v1", srcReg2, 2);
            String srcReg1 = getSrcReg1(src1);
            asm.addInstr("addu", "$v0", srcReg1, "$v1");
            asm.addInstr("lw", dstReg, "0($v0)");
        }else {
            asm.addInstr("sll", "$v0", srcReg2, 2);
            asm.addInstr("addu", "$v0", "$v0", "$fp");
            asm.addInstr("lw", dstReg, stackMap.get(src1)+"($v0)");
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
        String srcReg1 = getSrcReg1(src1);
        String srcReg2;
        if (src2.getType() == OpnumType.Imm) {
            asm.addInstr("li","$v1", src2);
            srcReg2 = "$v1";
        } else {
            srcReg2 = getDstReg(src2);
        }
        String instr = op.equals("==") ? "beq":
                       op.equals("!=") ? "bne":
                       op.equals("<")  ? "blz":
                       op.equals(">")  ? "bgz":
                       op.equals("<=") ? "ble":
                       op.equals(">=") ? "bge": null;
        asm.addInstr(instr, srcReg1, srcReg2, bk.getDst().toString());
        asm.addInstr("nop");
    }
}
