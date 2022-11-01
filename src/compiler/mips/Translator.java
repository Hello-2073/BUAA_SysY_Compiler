package compiler.mips;

import compiler.representation.module.BasicBlock;
import compiler.representation.module.Function;
import compiler.representation.module.Module;
import compiler.representation.quaternion.*;
import compiler.representation.quaternion.opnum.*;
import compiler.symbol.entry.VarEntry;

import java.util.HashMap;
import java.util.List;

public class Translator {
    private static Module module;
    private static Function function;

    private static Asm asm;

    private static Registers registers;

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
            asm.addLine("lw   \t" + reg + ", \t" + var.getName() + "($zero)");
        } else {
            asm.addLine("lw   \t" + reg + ", \t" + var.getOffset() + "($fp)");
        }
    }

    private static void saveVar(Var var, String reg) {
        if (var.isGlobal()) {
            asm.addLine("sw   \t" + reg + ", \t" + var.getName() + "($zero)");
        } else {
            asm.addLine("sw   \t" + reg + ", \t" + var.getOffset() + "($fp)");
        }
    }

    public static String getReg(Arg arg) {
        switch (arg.getType()) {
            case RetValue:
                return "$v0";
            case Tmp:
                if (tRegMap.containsKey((Tmp) arg)) {
                    return tRegMap.get(arg);
                } else {
                    String reg = "$t" + registers.allocTmp();
                    tRegMap.put((Tmp) arg, reg);
                    return reg;
                }
            case Var:
                if (sRegMap.containsKey((Var) arg)) {
                    return sRegMap.get(arg);
                }
                return null;
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
        asm.addLine("");
        asm.addLine(".text");
        asm.addLine("move \t$fp  \t$sp");
        asm.addLine("jal  \tmain");
        asm.addLine("nop");
        asm.addLine("li   \t$v0, \t10");
        asm.addLine("syscall");
        asm.addLine("");
        for (Function function : module.getFunctions()) {
            Translator.function = function;
            translate(function);
            asm.addLine("");
        }
    }

    private static void preview(Function function) {
        int space = function.getStackSpace();
        int paraNum = function.getFParaNum();
        asm.addLine(function.getName() + ":");
        asm.addLine("addi \t$sp,  \t$sp,  \t-" + space);
        asm.addLine("sw   \t$fp,  \t" + (space - 8) + "($sp)");
        asm.addLine("sw   \t$ra,  \t" + (space - 4) + "($sp)");
        for (int i = 0; i < paraNum; i++) {
            if (i <= 3) {
                asm.addLine("sw   \t$a" + i + ",  \t" + (4 * i) + "($sp)");
            } else {
                asm.addLine("lw   \t$v0  \t" + (space + 4 * (i - 4)) + "($sp)");
                asm.addLine("sw   \t$v0  \t" + (4 * i) + "($sp)");
            }
        }
        asm.addLine("move \t$fp,  \t$sp");
    }

    private static void translate(Function function) {
        registers.freeSave();
        sRegMap = new HashMap<>();
        preview(function);
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            translate(basicBlock);
        }
    }

    private static void translate(BasicBlock basicBlock) {
        tRegMap = new HashMap<>();
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
            default:
                break;
        }
    }

    private static void translate(Read read) {
        asm.addLine("li   \t$v0, \t5");
        asm.addLine("syscall");
        Arg dst = read.getDst();
        String dstReg = getReg(dst);
        if (dstReg != null) {
            asm.addLine("move \t" + dstReg + ",\t$v0");
        } else {
            saveVar((Var) dst, "$v0");
        }
    }

    private static void translate(Write write) {
        Arg src = write.getSrc();
        switch (src.getType()) {
            case Label:
                asm.addLine("la   \t" + "$a0,\t" + ((Label) src).getName());
                asm.addLine("li   \t$v0, \t4");
                asm.addLine("syscall");
                break;
            case Tmp:
            case Var:
            case RetValue:
                String reg = getReg(src);
                if (reg == null) {
                    loadVar((Var) src, "$a0");
                } else {
                    asm.addLine("move \t$a0, \t" + getReg(src));
                }
                asm.addLine("li   \t$v0, \t1");
                asm.addLine("syscall");
                break;
            default:
                throw new RuntimeException("print error " + src.getType());
        }
    }

    private static void translate(Single single) {
        String op = single.getOp();
        Arg dst = single.getDst();
        Arg src = single.getSrc();
        String dstReg = getReg(dst);
        String srcReg = getReg(src);
        switch (op) {
            case "+":
                if (src instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("li   \t" + dstReg + ",\t" + src);
                    } else {
                        asm.addLine("li   \t$v0, \t" + src);
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg == null) {
                        srcReg = "$v0";
                        loadVar((Var) src, srcReg);
                    }
                    if (dstReg != null) {
                        asm.addLine("move \t" + dstReg + ",\t" + srcReg);
                    } else {
                        asm.addLine("move \t$v0, \t" + srcReg);
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            case "-":
                if (srcReg == null) {
                    srcReg = "$v0";
                    loadVar((Var) src, "$v0");
                }
                if (dstReg != null) {
                    asm.addLine("negu \t" + dstReg + ",\t" + srcReg);
                } else {
                    asm.addLine("negu \t$v0, \t" + srcReg);
                    saveVar((Var) src, "$v0");
                }
                break;
            case "!":
                if (srcReg == null) {
                    srcReg = "$v0";
                    loadVar((Var) src, "$v0");
                }
                if (dstReg != null) {
                    asm.addLine("sne \t" + dstReg + ", \t" + srcReg + ", \t0");
                } else {
                    asm.addLine("sne \t$v0, \t" + srcReg + ", \t0");
                    saveVar((Var) src, "$v0");
                }
                break;
            default:
                break;
        }
    }

    private static void translate(Binary binary) {
        String op = binary.getOp();
        Arg dst = binary.getDst();
        Arg src1 = binary.getSrc1();
        Arg src2 = binary.getSrc2();
        String dstReg = getReg(dst);
        String srcReg1 = getReg(src1);
        if (srcReg1 == null) {
            srcReg1 = "$v0";
            loadVar((Var) src1, "$v0");
        }
        String srcReg2 = getReg(src2);
        switch (op) {
            case "+":
                if (src2 instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("addiu\t" + dstReg + ", \t" + srcReg1 + ", \t" + src2);
                    } else {
                        asm.addLine("addiu\t$v0, \t" + srcReg1 + ", \t" + src2);
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg2 == null) {
                        srcReg2 = "$v1";
                        loadVar((Var) src2, "$v1");
                    }
                    if (dstReg != null) {
                        asm.addLine("addu \t" + dstReg + ", \t" + srcReg1 + ", \t" + srcReg2);
                    } else {
                        asm.addLine("addu \t$v0, \t" + srcReg1 + ", \t" + srcReg2);
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            case "-":
                if (src2 instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("subiu\t" + dstReg + ", \t" + srcReg1 + ", \t" + src2);
                    } else {
                        asm.addLine("subiu\t$v0, \t" + srcReg1 + ", \t" + src2);
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg2 == null) {
                        srcReg2 = "$v1";
                        loadVar((Var) src2, "$v1");
                    }
                    if (dstReg != null) {
                        asm.addLine("subu \t" + dstReg + ", \t" + srcReg1 + ", \t" + srcReg2);
                    } else {
                        asm.addLine("subu \t$v0, \t" + srcReg1 + ", \t" + srcReg2);
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            case "*":
                if (src2 instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("mulu\t" + dstReg + ", \t" + srcReg1 + ", \t" + src2);
                    } else {
                        asm.addLine("mulu\t$v0, \t" + srcReg1 + ", \t" + src2);
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg2 == null) {
                        srcReg2 = "$v1";
                        loadVar((Var) src2, "$v1");
                    }
                    if (dstReg != null) {
                        asm.addLine("mulu \t" + dstReg + ", \t" + srcReg1 + ", \t" + srcReg2);
                    } else {
                        asm.addLine("mulu \t$v0, \t" + srcReg1 + ", \t" + srcReg2);
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            case "/":
                if (src2 instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("div  \t" + dstReg + ", \t" + srcReg1 + ", \t" + src2);
                    } else {
                        asm.addLine("div  \t$v0, \t" + srcReg1 + ", \t" + src2);
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg2 == null) {
                        srcReg2 = "$v1";
                        loadVar((Var) src2, "$v1");
                    }
                    if (dstReg != null) {
                        asm.addLine("div  \t" + dstReg + ", \t" + srcReg1 + ", \t" + srcReg2);
                    } else {
                        asm.addLine("div  \t$v0, \t" + srcReg1 + ", \t" + srcReg2);
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            case "%":
                if (src2 instanceof Imm) {
                    if (dstReg != null) {
                        asm.addLine("li   \t" + "$at, \t" + src2);
                        asm.addLine("div  \t" + srcReg1 + "  \t" + "$at");
                        asm.addLine("mfhi \t" + dstReg);
                    } else {
                        asm.addLine("li   \t" + "$at, \t" + src2);
                        asm.addLine("div  \t" + srcReg1 + "  \t" + "$at");
                        asm.addLine("mfhi \t$v0");
                        saveVar((Var) dst, "$v0");
                    }
                } else {
                    if (srcReg2 == null) {
                        srcReg2 = "$v1";
                        loadVar((Var) src2, "$v1");
                    }
                    if (dstReg != null) {
                        asm.addLine("div  \t" + srcReg1 + "  \t" + srcReg2);
                        asm.addLine("mfhi \t" + dstReg);
                    } else {
                        asm.addLine("div  \t" + srcReg1 + "  \t" + srcReg2);
                        asm.addLine("mfhi \t$v0");
                        saveVar((Var) dst, "$v0");
                    }
                }
                break;
            default:
                break;
        }
    }

    private static void translate(Call call) {
        for (Var arg : sRegMap.keySet()) {
            String reg = sRegMap.get(arg);
            asm.addLine("sw   \t" + reg + ",\t" + arg.getOffset() + "($fp)");
        }
        List<Arg> args = call.getArgs();
        for (int i = 0; i < args.size(); i++) {
            Arg arg = args.get(i);
            if (arg.getType() == OpnumType.Imm) {
                if (i <4) {
                    asm.addLine("li   \t$a" + i + ",  \t" + arg);
                } else {
                    asm.addLine("li   \t$v0, \t" + arg);
                    asm.addLine("sw   \t$v0, \t" +  (- 4 * (i - 3)) + "($sp)");
                }
            } else {
                String argReg = getReg(args.get(i));
                if (argReg == null) {
                    argReg = "$v0";
                    loadVar(((Var) args.get(i)), "$v0");
                }
                if (i <4) {
                    asm.addLine("move \t" + "$a" + i + ",  \t" + argReg);
                } else {
                    asm.addLine("sw   \t" + argReg + "  \t" +  (- 4 * (i - 3)) + "($sp)");
                }
            }
        }
        if (args.size() > 4) {
            asm.addLine("subiu\t$sp  \t$sp  \t" + 4 * (args.size() - 4));
        }
        asm.addLine("jal   \t" + call.getLabel());
        asm.addLine("nop");
        if (args.size() > 4) {
            asm.addLine("addiu\t$sp  \t$sp  \t" + 4 * (args.size() - 4));
        }
        for (Var arg : sRegMap.keySet()) {
            String reg = sRegMap.get(arg);
            asm.addLine("lw   \t" + reg + ",\t" + arg.getOffset() + "($fp)");
        }
    }

    private static void translate(Ret ret) {
        int space = function.getStackSpace();
        Arg retValue = ret.getSrc();
        if (retValue != null) {
            switch (retValue.getType()) {
                case Var:
                case Tmp:
                    String reg = getReg(retValue);
                    if (reg != null) {
                        asm.addLine("move \t$v0,\t" + reg);
                    } else {
                        loadVar((Var) retValue, "$v0");
                    }
                    break;
                case Imm:
                    asm.addLine("li   \t$v0,\t" + ((Imm) retValue).getValue());
            }
        }
        asm.addLine("lw   \t$ra,\t" + (space - 4) + "($fp)");
        asm.addLine("addiu\t$sp, \t$fp, \t" + space);
        asm.addLine("lw   \t$fp,\t" + (space - 8) + "($fp)");
        asm.addLine("jr   \t$ra");
        asm.addLine("nop");
    }
}
