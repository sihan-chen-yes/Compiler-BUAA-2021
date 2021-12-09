package MidCodeGeneration;

import Enum.DeclType;
import Enum.OpType;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import Optimizer.BasicBlock;
import Optimizer.DefPoint;
import Optimizer.FuncBlock;
import Optimizer.Optimizer;

import java.util.*;

public class MidCodeEntry {
    private OpType opType;
    private String r1;
    private String r2;
    private String r3;
    private String dst;

    private boolean isEntryPoint = false;

    private BasicBlock fatherBlock;

    private DefPoint gen;
    private HashSet<DefPoint> kill = new HashSet<>();
    private String curCode = "";


    public MidCodeEntry(OpType opType, String r1, String r2,String r3, String dst) {
        this.opType = opType;
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.dst = dst;
    }

    public OpType getOpType() {
        return opType;
    }

    @Override
    public String toString() {
        String midCode = null;
        if (opType == OpType.GLOBAL_DECLARE) {
            midCode = String.format("GLOBAL_DECLARE %s", dst);
        } else if (opType == OpType.FUNC_DECLARE) {
            midCode = String.format("FUNC_DECLARE %s", dst);
        } else if (opType == OpType.PUSH_PARAM) {
            midCode = String.format("PUSH_PARAM %s %s %s", r1,r2,dst);
        } else if (opType == OpType.LOAD_ARRAY_1D) {
            midCode = String.format("LOAD_ARRAY_1D %s %s %s", r1,r2,dst);
        } else if (opType == OpType.STORE_ARRAY_1D) {
            midCode = String.format("STORE_ARRAY_1D %s %s %s", r1,r2,dst);
        } else if (opType == OpType.LOAD_ARRAY_2D) {
            midCode = String.format("LOAD_ARRAY_2D %s %s %s %s", r1,r2,r3,dst);
        } else if (opType == OpType.STORE_ARRAY_2D) {
            midCode = String.format("STORE_ARRAY_2D %s %s %s %s", r1,r2,r3,dst);
        } else if (opType == OpType.LOAD_ARRDESS) {
            if (r2 == null) {
                midCode = String.format("LOAD_ADDRESS %s %s",r1,dst);
            } else {
                midCode = String.format("LOAD_ADDRESS %s %s %s",r1,r2,dst);
            }
        } else if (opType == OpType.ASSIGN) {
            midCode = String.format("ASSIGN %s %s", r1,dst);
        } else if (opType == OpType.PRINT_STRING) {
            midCode = String.format("PRINT_STRING %s", dst);
        } else if (opType == OpType.PRINT_INT) {
            midCode = String.format("PRINT_INT %s",dst);
        } else if (opType == OpType.RET_VALUE) {
            midCode = String.format("RET_VALUE %s", dst);
        } else if (opType == OpType.RET_VOID) {
            midCode = String.format("RET_VOID");
        } else if (opType == OpType.EXIT) {
            midCode = String.format("EXIT");
        } else if (opType == OpType.STORE_RET) {
            midCode = String.format("STORE_RET %s",dst);
        } else if (opType == OpType.GETINT) {
            midCode = String.format("GETINT %s", dst);
        } else if (opType == OpType.PREPARE_CALL) {
            midCode = String.format("PREPARE %s", dst);
        } else if (opType == OpType.CALL) {
            midCode = String.format("CALL %s", dst);
        } else if (opType == OpType.FIN_CALL) {
            midCode = String.format("FIN_CALL %s", dst);
        } else if (opType == OpType.ADD) {
            midCode = String.format("ADD %s %s %s", r1,r2,dst);
        } else if (opType == OpType.SUB) {
            midCode = String.format("SUB %s %s %s", r1,r2,dst);
        } else if (opType == OpType.MULT) {
            midCode = String.format("MULT %s %s %s", r1,r2,dst);
        } else if (opType == OpType.DIV) {
            midCode = String.format("DIV %s %s %s", r1,r2,dst);
        } else if (opType == OpType.MOD) {
            midCode = String.format("MOD %s %s %s", r1,r2,dst);
        } else if (opType == OpType.NEG) {
            midCode = String.format("NEG %s %s",r1,dst);
        } else if (opType == OpType.SLT) {
            midCode = String.format("SLT %s %s %s",r1,r2,dst);
        } else if (opType == OpType.SGT) {
            midCode = String.format("SGT %s %s %s",r1,r2,dst);
        } else if (opType == OpType.SLE) {
            midCode = String.format("SLE %s %s %s",r1,r2,dst);
        } else if (opType == OpType.SGE) {
            midCode = String.format("SGE %s %s %s",r1,r2,dst);
        } else if (opType == OpType.SNE) {
            midCode = String.format("SNE %s %s %s",r1,r2,dst);
        } else if (opType == OpType.SEQ) {
            midCode = String.format("SEQ %s %s %s",r1,r2,dst);
        } else if (opType == OpType.NOT) {
            midCode = String.format("NOT %s %s",r1,dst);
        } else if (opType == OpType.LABEL_GEN) {
            midCode = String.format("LABEL_GEN %s",dst);
        } else if (opType == OpType.BEQZ) {
            midCode = String.format("BEQZ %s %s",r1,dst);
        } else if (opType == OpType.BNEZ) {
            midCode = String.format("BNEZ %s %s",r1,dst);
        } else if (opType == OpType.GOTO) {
            midCode = String.format("GOTO %s",dst);
        }
        return midCode;
    }

    public String toTargetCode() {
        String start = String.format("#############") + toString() + String.format("#############\n");
        String end = "\n";
        curCode += start;
        if (opType == OpType.GLOBAL_DECLARE) {
            genStoreGlobal();
        }else if (opType == OpType.FUNC_DECLARE) {
            MidCodeGener.startFuncDef(dst);
            genFuncLabel();
        } else if (opType == OpType.PUSH_PARAM) {
            genPushParam();
        } else if (opType == OpType.STORE_RET) {
            genStoreRet();
        } else if (opType == OpType.LOAD_ARRAY_1D) {
            genLoadArray1D();
        } else if (opType == OpType.STORE_ARRAY_1D) {
            genStoreArray1D();
        } else if (opType == OpType.LOAD_ARRAY_2D) {
            genLoadArray2D();
        } else if (opType == OpType.STORE_ARRAY_2D) {
            genStoreArray2D();
        } else if (opType == OpType.LOAD_ARRDESS) {
            genLoadAddress();
        } else if (opType == OpType.ASSIGN) {
            genAssign();
        } else if (opType == OpType.PRINT_STRING) {
            genPrintStr();
        } else if (opType == OpType.PRINT_INT) {
            genPrintInt();
        } else if (opType == OpType.RET_VALUE) {
            genRetValue();
        } else if (opType == OpType.RET_VOID) {
            genRetVoid();
        } else if (opType == OpType.EXIT) {
            genExit();
        } else if (opType == OpType.GETINT) {
            genGetInt();
        } else if (opType == OpType.PREPARE_CALL) {
            genPrePareCall();
        } else if (opType == OpType.CALL) {
            genCall();
        } else if (opType == OpType.FIN_CALL) {
            genFinCall();
        } else if (opType == OpType.ADD) {
            genDoubleOperand();
        } else if (opType == OpType.SUB) {
            genDoubleOperand();
        } else if (opType == OpType.MULT) {
            genDoubleOperand();
        } else if (opType == OpType.DIV) {
            genDoubleOperand();
        } else if (opType == OpType.MOD) {
            genDoubleOperand();
        } else if (opType == OpType.NEG) {
            genSingleOperand();
        } else if (opType == OpType.SLT) {
            genDoubleOperand();
        } else if (opType == OpType.SGT) {
            genDoubleOperand();
        } else if (opType == OpType.SLE) {
            genDoubleOperand();
        } else if (opType == OpType.SGE) {
            genDoubleOperand();
        } else if (opType == OpType.SEQ) {
            genDoubleOperand();
        } else if (opType == OpType.SNE) {
            genDoubleOperand();
        } else if (opType == OpType.NOT) {
            genSingleOperand();
        } else if (opType == OpType.LABEL_GEN) {
            genLabel();
        } else if (opType == OpType.BEQZ) {
            genBeqz();
        } else if (opType == OpType.BNEZ) {
            genBnez();
        } else {
            assert opType == OpType.GOTO;
            genGoto();
        }
        curCode += end;
        return curCode;
    }

    public void genStoreGlobal() {
        curCode += String.format("%s:.word ",dst);
        SymbolTableEntry symbolTableEntry = MidCodeGener.getSymbolTable().search_global(dst);
        ArrayList<Integer> flattenValues = symbolTableEntry.getFlattenValues();
        curCode += flattenValues.get(0).toString();
        for (int i = 1;i < flattenValues.size();i++) {
            curCode += String.format(",%s",flattenValues.get(i).toString());
        }
    }

    public void genFuncLabel() {
        curCode += String.format("%s:",dst);
    }

    public void loadParam() {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        //现在位于的函数体
        int size = symbolTable.getLocalSize(dst);
        //要调用的函数体的大小
        int arg_offset = size - (Integer.parseInt(r2) + 1) * 4;
        //压入参数的位置
        if (!Optimizer.isOp()) {
            if (symbolTable.isNumber(func,r1)) {
                //是常数
                curCode += String.format("li $t0,%s",r1);
                curCode += "\n";
                curCode += String.format("sw $t0,%d($sp)",arg_offset);
            } else {
                //是局部变量
                if (symbolTable.isLocal(func,r1)) {
                    int offset = symbolTable.searchOffset_sp(func,r1);
                    //相对于当前sp的offset
                    curCode += String.format("lw $t0,%d($sp)",offset);
                    curCode += "\n";
                    curCode += String.format("sw $t0,%d($sp)",arg_offset);
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("lw $t0,%d($gp)",offset);
                    curCode += "\n";
                    curCode += String.format("sw $t0,%d($sp)",arg_offset);
                }
            }
        } else {
            if (symbolTable.isNumber(func,r1)) {
                //是常数
                curCode += String.format("li $t0,%s",r1);
                curCode += "\n";
                curCode += String.format("sw $t0,%d($sp)",arg_offset);
            } else {
                //是局部变量
                if (symbolTable.isLocal(func,r1)) {
                    int offset = symbolTable.searchOffset_sp(func,r1);
                    //相对于当前sp的offset
                    if (fatherBlock.hasSReg(r1)) {
                        curCode += String.format("sw %s,%d($sp)",fatherBlock.querySReg(r1),arg_offset);
                    } else {
                        curCode += String.format("lw $t0,%d($sp)",offset);
                        curCode += "\n";
                        curCode += String.format("sw $t0,%d($sp)",arg_offset);
                    }
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("lw $t0,%d($gp)",offset);
                    curCode += "\n";
                    curCode += String.format("sw $t0,%d($sp)",arg_offset);
                }
            }
        }
    }

    public void genPushParam() {
        //只可能是常数或者局部变量 或者全局变量
        loadParam();
    }

    public void genStoreRet() {
        //默认返回值在v0 默认将v0放在t reg中
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int offset = symbolTable.searchOffset_sp(func,dst);
        curCode += String.format("sw $v0,%d($sp)",offset);
        //Todo 寄存器分配 不要用v0
    }

    public String load(String name) {
        //将四元式中的Ident或者常数load到t0 或者 其他s中
        //Todo 分配其他reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String reg;
        if (!Optimizer.isOp()) {
            reg = "$t0";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t0,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("lw $t0,%d($sp)",offset);
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t0,%d($gp)",offset);
                }
            }
        } else {
            reg = "$t0";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t0,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    if (fatherBlock.hasSReg(name)) {
                        reg = fatherBlock.querySReg(name);
                    } else {
                        curCode += String.format("lw $t0,%d($sp)",offset);
                    }
                    //Todo tReg
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t0,%d($gp)",offset);
                }
            }
        }
        return reg;
    }

    public void store(String name,String regVal) {
        //将t0存到对应的Ident中
        //Todo 分配其他reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (!Optimizer.isOp()) {
            if (symbolTable.isLocal(func,name)) {
                int offset = symbolTable.searchOffset_sp(func,name);
                curCode += String.format("sw $t0,%d($sp)",offset);
            } else {
                assert symbolTable.isGlobal(name);
                int offset = symbolTable.searchOffset_gp(name);
                curCode += String.format("sw $t0,%d($gp)",offset);
            }
        } else {
            assert regVal != null;
            if (symbolTable.isLocal(func,name)) {
                int offset = symbolTable.searchOffset_sp(func,name);
                curCode += String.format("sw %s,%d($sp)",regVal,offset);
            } else {
                assert symbolTable.isGlobal(name);
                int offset = symbolTable.searchOffset_gp(name);
                curCode += String.format("sw %s,%d($gp)",regVal,offset);
            }
        }
    }

    public void calOffset1D(String i,String reg0) {
        // t0 = t0 + t1 i is T 算出绝对地址 存到t0 或者其他s
        if (!Optimizer.isOp()) {
            loadSec(i);
            curCode += "\n";
            curCode += String.format("sll $t1,$t1,2");
            curCode += "\n";
            curCode += String.format("addu $t0,$t0,$t1");
        } else {
            assert reg0 != null;
            String reg1;
            reg1 = loadSec(i);
            curCode += "\n";
            curCode += String.format("sll %s,%s,2",reg1,reg1);
            curCode += "\n";
            curCode += String.format("addu %s,%s,%s",reg0,reg0,reg1);
        }
    }

    public void calOffset2D(String i, String j, int length2D,String reg0) {
        //i j is T t1 t2 算出绝对地址放到t0 或者其他s
        //t0 = t0 + (t1 * t2 + t2) * 4
        String reg1,reg2;
        if (!Optimizer.isOp()) {
            loadSec(i);
            curCode += "\n";
            curCode += String.format("li $t2,%d",length2D);
            curCode += "\n";
            curCode += String.format("mult $t1,$t2");
            curCode += "\n";
            curCode += String.format("mflo $t1");
            curCode += "\n";
            loadThird(j);
            curCode += "\n";
            curCode += String.format("addu $t1,$t1,$t2");
            curCode += "\n";
            curCode += String.format("sll $t1,$t1,2");
            curCode += "\n";
            curCode += String.format("addu $t0,$t0,$t1");
        } else {
            assert reg0 != null;
            reg1 = loadSec(i);
            curCode += "\n";
            //Todo t2 change
            curCode += String.format("li $t2,%d",length2D);
            curCode += "\n";
            curCode += String.format("mult %s,$t2",reg1);
            curCode += "\n";
            curCode += String.format("mflo %s",reg1);
            curCode += "\n";
            reg2 = loadThird(j);
            curCode += "\n";
            curCode += String.format("addu %s,%s,%s",reg1,reg1,reg2);
            curCode += "\n";
            curCode += String.format("sll %s,%s,2",reg1,reg1);
            curCode += "\n";
            curCode += String.format("addu %s,%s,%s",reg0,reg0,reg1);
        }
    }

    public void calOffsetAddr(String i,int length2D,String reg0) {
        //t0 = t0 + t1 * t2 *4
        if (!Optimizer.isOp()) {
            loadSec(i);
            //$t1
            curCode += "\n";
            curCode += String.format("li $t2,%d",length2D);
            curCode += "\n";
            curCode += String.format("mult $t1,$t2");
            curCode += "\n";
            curCode += String.format("mflo $t1");
            curCode += "\n";
            curCode += String.format("sll $t1,$t1,2");
            curCode += "\n";
            curCode += String.format("addu $t0,$t0,$t1");
        } else {
            assert reg0 != null;
            String reg1;
            reg1 = loadSec(i);
            //$t1
            curCode += "\n";
            //Todo t change
            curCode += String.format("li $t2,%d",length2D);
            curCode += "\n";
            curCode += String.format("mult %s,$t2",reg1);
            curCode += "\n";
            curCode += String.format("mflo %s",reg1);
            curCode += "\n";
            curCode += String.format("sll %s,%s,2",reg1,reg1);
            curCode += "\n";
            curCode += String.format("addu %s,%s,%s",reg0,reg0,reg1);
        }
    }

    public String load1D(String name,String i) {
        //可能是全局或者局部 加载到t0 后续优化reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String reg;
        if (!Optimizer.isOp()) {
            if (symbolTable.isLocal(func,name)) {
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    //参数（存的是地址）
                    load(name);
                    curCode += "\n";
                    calOffset1D(i,null);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0($t0)");
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    calOffset1D(i,null);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0($t0)");
                }
            } else {
                assert symbolTable.isGlobal(name);
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                calOffset1D(i,null);
                curCode += "\n";
                curCode += String.format("lw $t0,0($t0)");
            }
            reg = "$t0";
        } else {
            if (symbolTable.isLocal(func,name)) {
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    //参数（存的是地址）
                    reg = load(name);
                    curCode += "\n";
                    calOffset1D(i,reg);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0(%s)",reg);
                    //Todo change t
                    reg = "$t0";
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    reg = "$t0";
                    calOffset1D(i,reg);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0(%s)",reg);
                    reg = "$t0";
                }
            } else {
                assert symbolTable.isGlobal(name);
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                reg = "$t0";
                calOffset1D(i,reg);
                curCode += "\n";
                curCode += String.format("lw $t0,0(%s)",reg);
                reg = "$t0";
            }
        }
        return reg;
    }

    public String load2D(String name,String i,String j) {
        //可能是全局或者局部 加载到t0 后续优化reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int length2D;
        String reg;
        if (!Optimizer.isOp()) {
            if (symbolTable.isLocal(func,name)) {
                length2D = symbolTable.search_local(func,name).getLength2D();
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    load(name);
                    curCode += "\n";
                    calOffset2D(i,j,length2D,null);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0($t0)");
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    calOffset2D(i,j,length2D,null);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0($t0)");
                }
            } else {
                assert symbolTable.isGlobal(name);
                length2D = symbolTable.search_global(name).getLength2D();
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode = String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                calOffset2D(i,j,length2D,null);
                curCode += "\n";
                curCode += String.format("lw $t0,0($t0)");
            }
            reg = "$t0";
        } else {
            if (symbolTable.isLocal(func,name)) {
                length2D = symbolTable.search_local(func,name).getLength2D();
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    reg = load(name);
                    curCode += "\n";
                    calOffset2D(i,j,length2D,reg);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0(%s)",reg);
                    //Todo t change
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    reg = "$t0";
                    calOffset2D(i,j,length2D,reg);
                    curCode += "\n";
                    curCode += String.format("lw $t0,0(%s)",reg);
                    reg = "$t0";
                }
            } else {
                assert symbolTable.isGlobal(name);
                length2D = symbolTable.search_global(name).getLength2D();
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode = String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                reg = "$t0";
                calOffset2D(i,j,length2D,reg);
                curCode += "\n";
                curCode += String.format("lw $t0,0(%s)",reg);
                reg = "$t0";
            }
        }
        return reg;
    }

    public void store1D(String name,String i,String regVal) {
        //Todo 常量传播可优化
        //store时t0 已被用 后续优化reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String reg;
        if (!Optimizer.isOp()) {
            if (symbolTable.isLocal(func,name)) {
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    //参数（存的是地址）
                    curCode += String.format("move $t3,$t0");
                    //$t0此时已经用了
                    curCode += "\n";
                    load(name);
                    curCode += "\n";
                    calOffset1D(i,null);
                    curCode += "\n";
                    curCode += String.format("sw $t3,0($t0)");
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("move $t3,$t0");
                    curCode += "\n";
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    calOffset1D(i,null);
                    curCode += "\n";
                    curCode += String.format("sw $t3,0($t0)");
                }
            } else {
                assert symbolTable.isGlobal(name);
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode += String.format("move $t3,$t0");
                curCode += "\n";
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                calOffset1D(i,null);
                curCode += "\n";
                curCode += String.format("sw $t3,0($t0)");
            }
        } else {
            if (symbolTable.isLocal(func,name)) {
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    //参数（存的是地址）
                    if (regVal.equals("$t0")) {
                        curCode += String.format("move $t3,$t0");
                        regVal = "$t3";
                    }
                    //Todo t change
                    //$t0此时已经用了
                    curCode += "\n";
                    reg = load(name);
                    curCode += "\n";
                    calOffset1D(i,reg);
                    curCode += "\n";
                    curCode += String.format("sw %s,0(%s)",regVal,reg);
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    if (regVal.equals("$t0")) {
                        curCode += String.format("move $t3,$t0");
                        regVal = "$t3";
                    }
                    curCode += "\n";
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    reg = "$t0";
                    calOffset1D(i,reg);
                    curCode += "\n";
                    curCode += String.format("sw %s,0(%s)",regVal,reg);
                }
            } else {
                assert symbolTable.isGlobal(name);
                int offset_gp = symbolTable.searchOffset_gp(name);
                if (regVal.equals("$t0")) {
                    curCode += String.format("move $t3,$t0");
                    regVal = "$t3";
                }
                curCode += "\n";
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                reg = "$t0";
                calOffset1D(i,reg);
                curCode += "\n";
                curCode += String.format("sw %s,0(%s)",regVal,reg);
            }
        }
    }

    public void store2D(String name,String i,String j,String regVal) {
        //Todo
        //store时t0 已被用 后续优化reg
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int length2D;
        String reg;
        if (!Optimizer.isOp()) {
            if (symbolTable.isLocal(func,name)) {
                length2D = symbolTable.search_local(func,name).getLength2D();
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    curCode += String.format("move $t3,$t0");
                    curCode += "\n";
                    load(name);//得到地址
                    curCode += "\n";
                    calOffset2D(i,j,length2D,null);
                    curCode += "\n";
                    curCode += String.format("sw $t3,0($t0)");
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("move $t3,$t0");
                    curCode += "\n";
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    calOffset2D(i,j,length2D,null);
                    curCode += "\n";
                    curCode += String.format("sw $t3,0($t0)");
                }
            } else {
                assert symbolTable.isGlobal(name);
                length2D = symbolTable.search_global(name).getLength2D();
                int offset_gp = symbolTable.searchOffset_gp(name);
                curCode += String.format("move $t3,$t0");
                curCode += "\n";
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                calOffset2D(i,j,length2D,null);
                curCode += "\n";
                curCode += String.format("sw $t3,0($t0)");
            }
        } else {
            if (symbolTable.isLocal(func,name)) {
                length2D = symbolTable.search_local(func,name).getLength2D();
                if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                    if (regVal.equals("$t0")) {
                        curCode += String.format("move $t3,$t0");
                        regVal = "$t3";
                    }
                    curCode += "\n";
                    reg = load(name);//得到地址
                    curCode += "\n";
                    calOffset2D(i,j,length2D,reg);
                    curCode += "\n";
                    curCode += String.format("sw %s,0(%s)",regVal,reg);
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,name);
                    if (regVal.equals("$t0")) {
                        curCode += String.format("move $t3,$t0");
                        regVal = "$t3";
                    }
                    curCode += "\n";
                    curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                    curCode += "\n";
                    reg = "$t0";
                    calOffset2D(i,j,length2D,reg);
                    curCode += "\n";
                    curCode += String.format("sw %s,0(%s)",regVal,reg);
                }
            } else {
                assert symbolTable.isGlobal(name);
                length2D = symbolTable.search_global(name).getLength2D();
                int offset_gp = symbolTable.searchOffset_gp(name);
                if (regVal.equals("$t0")) {
                    curCode += String.format("move $t3,$t0");
                    regVal = "$t3";
                }
                curCode += "\n";
                curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                curCode += "\n";
                reg = "$t0";
                calOffset2D(i,j,length2D,reg);
                curCode += "\n";
                curCode += String.format("sw %s,0(%s)",regVal,reg);
            }
        }
    }

    public void genLoadArray1D() {
        String reg;
        if (!Optimizer.isOp()) {
            load1D(r1,r2);
            curCode += "\n";
            store(dst,null);
        } else {
            //Todo 直接load到变量中 如果还有reg
            reg = load1D(r1,r2);
            curCode += "\n";
            store(dst,reg);
            //T0在内存中
            //Todo change t
        }
    }

    public void genStoreArray1D() {
        //Todo 此处可优化
        String regVal;
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
            } else {
                load(dst);
            }
            curCode += "\n";
            store1D(r1,r2,null);
        } else {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
                //Todo t0 change
                regVal = "$t0";
            } else {
                regVal = load(dst);
            }
            curCode += "\n";
            store1D(r1,r2,regVal);
        }
    }

    public void genLoadArray2D() {
        String reg;
        if (!Optimizer.isOp()) {
            load2D(r1,r2,r3);
            curCode += "\n";
            store(dst,null);
        } else {
            //Todo 直接load到T0 不要存到内存
            reg = load2D(r1,r2,r3);
            curCode += "\n";
            store(dst,reg);
        }
    }

    public void genStoreArray2D() {
        //Todo 此处可优化
        String regVal;
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
            } else {
                load(dst);
            }
            curCode += "\n";
            store2D(r1,r2,r3,null);
        } else {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
                regVal = "$t0";
            } else {
                regVal = load(dst);
            }
            curCode += "\n";
            store2D(r1,r2,r3,regVal);
        }
    }

    public void genLoadAddress() {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        //Todo 直接把地址给T
        if (!Optimizer.isOp()) {
            if (r2 == null) {
                if (symbolTable.isLocal(func,r1)) {
                    if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                        load(r1);
                        curCode += "\n";
                        store(dst,null);
                    } else {
                        int offset_sp = symbolTable.searchOffset_sp(func,r1);
                        curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                        //算出地址
                        curCode += "\n";
                        store(dst,null);
                    }
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset_gp = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                    //算出地址
                    curCode += "\n";
                    store(dst,null);
                }
            } else {
                //r2 不为0
                if (symbolTable.isLocal(func,r1)) {
                    //Todo 直接load到T中
                    if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                        load(r1);
                        //参数中的地址 放在t0
                        curCode += "\n";
                        int length2D = symbolTable.search_local(func,r1).getLength2D();
                        calOffsetAddr(r2,length2D,null);
                        //$t0就是地址
                        curCode += "\n";
                        store(dst,null);
                    } else {
                        int offset_sp = symbolTable.searchOffset_sp(func,r1);
                        curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                        int length2D = symbolTable.search_local(func,r1).getLength2D();
                        curCode += "\n";
                        calOffsetAddr(r2,length2D,null);
                        //算出地址
                        curCode += "\n";
                        store(dst,null);
                    }
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset_gp = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                    int length2D = symbolTable.search_global(r1).getLength2D();
                    curCode += "\n";
                    calOffsetAddr(r2,length2D,null);
                    //算出地址
                    curCode += "\n";
                    store(dst,null);
                }
            }
        } else {
            String regVal;
            if (r2 == null) {
                if (symbolTable.isLocal(func,r1)) {
                    if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                        regVal = load(r1);
                        curCode += "\n";
                        store(dst,regVal);
                    } else {
                        int offset_sp = symbolTable.searchOffset_sp(func,r1);
                        curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                        //算出地址
                        curCode += "\n";
                        regVal = "$t0";
                        store(dst,regVal);
                    }
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset_gp = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                    //算出地址
                    curCode += "\n";
                    regVal = "$t0";
                    store(dst,regVal);
                }
            } else {
                if (symbolTable.isLocal(func,r1)) {
                    //Todo
                    if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                        regVal = load(r1);
                        //参数中的地址 放在t0
                        curCode += "\n";
                        int length2D = symbolTable.search_local(func,r1).getLength2D();
                        calOffsetAddr(r2,length2D,regVal);
                        //$t0就是地址
                        curCode += "\n";
                        store(dst,regVal);
                    } else {
                        int offset_sp = symbolTable.searchOffset_sp(func,r1);
                        curCode += String.format("addiu $t0,$sp,%d",offset_sp);
                        int length2D = symbolTable.search_local(func,r1).getLength2D();
                        curCode += "\n";
                        regVal = "$t0";
                        calOffsetAddr(r2,length2D,regVal);
                        //算出地址
                        curCode += "\n";
                        store(dst,regVal);
                    }
                } else {
                    assert symbolTable.isGlobal(r1);
                    int offset_gp = symbolTable.searchOffset_gp(r1);
                    curCode += String.format("addiu $t0,$gp,%d",offset_gp);
                    int length2D = symbolTable.search_global(r1).getLength2D();
                    curCode += "\n";
                    regVal = "$t0";
                    calOffsetAddr(r2,length2D,regVal);
                    //算出地址
                    curCode += "\n";
                    store(dst,regVal);
                }
            }
        }

    }

    public void genAssign() {
        //Todo 此处可优化
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
            } else {
                load(dst);
            }
            curCode += "\n";
            store(r1,null);
        } else {
            String regVal;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
                regVal = "$t0";
            } else {
                regVal = load(dst);
            }
            curCode += "\n";
            store(r1,regVal);
        }
    }

    public void genPrintStr() {
        //Todo 寄存器冲突 a0 v0要不要看作普通reg
        if (!Optimizer.isOp()) {
            curCode += String.format("la $a0,%s",dst);
            curCode += "\n";
            curCode += String.format("li $v0,4");
            curCode += "\n";
            curCode += String.format("syscall");
        } else {

        }
    }

    public void genPrintInt() {
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
            } else {
                load(dst);
            }
            curCode += "\n";
            curCode += String.format("move $a0,$t0");
            curCode += "\n";
            curCode += String.format("li $v0,1");
            curCode += "\n";
            curCode += String.format("syscall");
        } else {
            String regVal;
            //Todo 此处可优化t
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
                regVal = "$t0";
            } else {
                regVal = load(dst);
            }
            curCode += "\n";
            curCode += String.format("move $a0,%s",regVal);
            curCode += "\n";
            curCode += String.format("li $v0,1");
            curCode += "\n";
            curCode += String.format("syscall");
        }
    }

    public void genRetValue() {
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
            } else {
                load(dst);
            }
            curCode += "\n";
            curCode += String.format("move $v0,$t0");
            curCode += "\n";
            curCode += String.format("jr $ra");
        } else {
            //Todo 此处可优化t
            String regVal;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
                curCode += String.format("li $t0,%s",dst);
                regVal = "$t0";
            } else {
                regVal = load(dst);
            }
            curCode += "\n";
            curCode += String.format("move $v0,%s",regVal);
            curCode += "\n";
            curCode += String.format("jr $ra");
        }
    }

    public void genRetVoid() {
        curCode += String.format("jr $ra");
    }

    public void genGetInt() {
        if (!Optimizer.isOp()) {
            curCode += String.format("li $v0,5");
            curCode += "\n";
            curCode += String.format("syscall");
            curCode += "\n";
            curCode += String.format("move $t0,$v0");
            curCode += "\n";
            store(dst,null);
        } else {
            String regVal;
            //Todo 此处可优化t
            curCode += String.format("li $v0,5");
            curCode += "\n";
            curCode += String.format("syscall");
            curCode += "\n";
            curCode += String.format("move $t0,$v0");
            regVal = "$t0";
            curCode += "\n";
            store(dst,regVal);
        }
    }

    public void genPrePareCall() {
        int size = MidCodeGener.getSymbolTable().getLocalSize(dst);
        if (!Optimizer.isOp()) {
            curCode += String.format("sw $ra,0($sp)");
            curCode += "\n";
            curCode += String.format("addiu $sp,$sp,-%d",size);
            MidCodeGener.getSymbolTable().addStack_size(size);
        } else {
            curCode += String.format("sw $ra,0($sp)");
            curCode += "\n";
            genSaveSRegs();
            curCode += "\n";
            curCode += String.format("addiu $sp,$sp,-%d",size);
            MidCodeGener.getSymbolTable().addStack_size(size);
        }
        //Todo save  t a
    }

    public void genSaveSRegs() {
        HashMap<String,String> varToReg = fatherBlock.getVarToReg();
        Iterator<Map.Entry<String, String>> iterator = varToReg.entrySet().iterator();
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        while (iterator.hasNext()) {
            String var = iterator.next().getKey();
            String reg = iterator.next().getValue();
            int offset_sp = symbolTable.searchOffset_sp(func,var);
            curCode += String.format("sw %s,%d($sp)",reg,offset_sp);
            if (iterator.hasNext()) {
                curCode += "\n";
            }
        }
    }

    public void genCall() {
        curCode += String.format("jal %s",dst);
    }

    public void genFinCall() {
        //Todo 恢复 s t a
        int size = MidCodeGener.getSymbolTable().getLocalSize(dst);
        if (!Optimizer.isOp()) {
            curCode += String.format("addiu $sp,$sp,%d",size);
            MidCodeGener.getSymbolTable().subStack_size(size);
            curCode += "\n";
            curCode += String.format("lw $ra,0($sp)");
        } else {
            curCode += String.format("addiu $sp,$sp,%d",size);
            MidCodeGener.getSymbolTable().subStack_size(size);
            curCode += "\n";
            curCode += String.format("lw $ra,0($sp)");
            curCode += "\n";
            genLoadSRegs();
        }
    }

    public void genLoadSRegs() {
        HashMap<String,String> varToReg = fatherBlock.getVarToReg();
        Iterator<Map.Entry<String, String>> iterator = varToReg.entrySet().iterator();
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        while (iterator.hasNext()) {
            String var = iterator.next().getKey();
            String reg = iterator.next().getValue();
            int offset_sp = symbolTable.searchOffset_sp(func,var);
            curCode += String.format("lw %s,%d($sp)",reg,offset_sp);
            if (iterator.hasNext()) {
                curCode += "\n";
            }
        }
    }

    public void genExit() {
        curCode += String.format("li $v0,10");
        curCode += "\n";
        curCode += String.format("syscall");
    }

    public String loadSec(String name) {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String reg;
        if (!Optimizer.isOp()) {
            reg = "$t1";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t1,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("lw $t1,%d($sp)",offset);
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t1,%d($gp)",offset);
                }
            }
        } else {
            reg = "$t1";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t1,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    if (fatherBlock.hasSReg(name)) {
                        reg = fatherBlock.querySReg(name);
                    } else {
                        curCode += String.format("lw $t1,%d($sp)",offset);
                    }
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t1,%d($gp)",offset);
                }
            }
        }
        return reg;
    }

    public String loadThird(String name) {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String reg;
        if (!Optimizer.isOp()) {
            reg = "$t2";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t2,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    curCode += String.format("lw $t2,%d($sp)",offset);
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t2,%d($gp)",offset);
                }
            }
        } else {
            reg = "$t2";
            if (symbolTable.isNumber(func,name)) {
                curCode += String.format("li $t2,%d",Integer.parseInt(name));
            } else {
                if (symbolTable.isLocal(func,name)) {
                    int offset = symbolTable.searchOffset_sp(func,name);
                    if (fatherBlock.hasSReg(name)) {
                        reg = fatherBlock.querySReg(name);
                    } else {
                        curCode += String.format("lw $t2,%d($sp)",offset);
                    }
                } else {
                    assert symbolTable.isGlobal(name);
                    int offset = symbolTable.searchOffset_gp(name);
                    curCode += String.format("lw $t2,%d($gp)",offset);
                }
            }
        }
        return reg;
    }

    public void genDoubleOperand() {
        assert isDoubleOp();
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
            } else {
                load(r1);
            }
            curCode += "\n";
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
                curCode += String.format("li $t1,%s",r2);
            } else {
                loadSec(r2);
            }
            curCode += "\n";
            if (opType == OpType.ADD) {
                curCode += String.format("addu $t0,$t0,$t1");
            } else if (opType == OpType.SUB) {
                curCode += String.format("subu $t0,$t0,$t1");
            } else if (opType == OpType.MULT) {
                curCode += String.format("mult $t0,$t1");
                curCode += "\n";
                curCode += String.format("mflo $t0");
            } else if (opType == OpType.DIV) {
                curCode += String.format("div $t0,$t1");
                curCode += "\n";
                curCode += String.format("mflo $t0");
            } else if (opType == OpType.MOD) {
                curCode += String.format("div $t0,$t1");
                curCode += "\n";
                curCode += String.format("mfhi $t0");
            } else if (opType == OpType.SLT) {
                curCode += String.format("slt $t0,$t0,$t1");
            } else if (opType == OpType.SLE) {
                curCode += String.format("sgt $t0,$t0,$t1");
                curCode += "\n";
                //取反
                curCode += String.format("seq $t0,$t0,$0");
            } else if (opType == OpType.SGT) {
                curCode += String.format("sgt $t0,$t0,$t1");
            } else if (opType == OpType.SGE) {
                curCode += String.format("slt $t0,$t0,$t1");
                curCode += "\n";
                //取反
                curCode += String.format("seq $t0,$t0,$0");
            } else if (opType == OpType.SEQ) {
                curCode += String.format("seq $t0,$t0,$t1");
            } else if (opType == OpType.SNE) {
                curCode += String.format("sne $t0,$t0,$t1");
            }
            curCode += "\n";
            store(dst,null);
        } else {
            //Todo t优化
            String reg0,reg1;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
                reg0 = "$t0";
            } else {
                reg0 = load(r1);
            }
            curCode += "\n";
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
                curCode += String.format("li $t1,%s",r2);
                reg1 = "$t1";
            } else {
                reg1 = loadSec(r2);
            }
            curCode += "\n";
            if (opType == OpType.ADD) {
                curCode += String.format("addu %s,%s,%s",reg0,reg0,reg1);
            } else if (opType == OpType.SUB) {
                curCode += String.format("subu %s,%s,%s",reg0,reg0,reg1);
            } else if (opType == OpType.MULT) {
                curCode += String.format("mult %s,%s",reg0,reg1);
                curCode += "\n";
                curCode += String.format("mflo %s",reg0);
            } else if (opType == OpType.DIV) {
                curCode += String.format("div %s,%s",reg0,reg1);
                curCode += "\n";
                curCode += String.format("mflo %s",reg0);
            } else if (opType == OpType.MOD) {
                curCode += String.format("div %s,%s",reg0,reg1);
                curCode += "\n";
                curCode += String.format("mfhi %s",reg0);
            } else if (opType == OpType.SLT) {
                curCode += String.format("slt %s,%s,%s",reg0,reg0,reg1);
            } else if (opType == OpType.SLE) {
                curCode += String.format("sgt %s,%s,%s",reg0,reg0,reg1);
                curCode += "\n";
                //取反
                curCode += String.format("seq %s,%s,$0",reg0,reg0);
            } else if (opType == OpType.SGT) {
                curCode += String.format("sgt %s,%s,%s",reg0,reg0,reg1);
            } else if (opType == OpType.SGE) {
                curCode += String.format("slt %s,%s,%s",reg0,reg0,reg1);
                curCode += "\n";
                //取反
                curCode += String.format("seq %s,%s,$0",reg0,reg0);
            } else if (opType == OpType.SEQ) {
                curCode += String.format("seq %s,%s,%s",reg0,reg0,reg1);
            } else if (opType == OpType.SNE) {
                curCode += String.format("sne %s,%s,%s",reg0,reg0,reg1);
            }
            curCode += "\n";
            //直接给另一个T
            store(dst,reg0);
        }
    }

    public void genSingleOperand() {
        assert isSingleOp();
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
            } else {
                load(r1);
            }
            curCode += "\n";
            if (opType == OpType.NEG) {
                curCode += String.format("neg $t0,$t0");
            } else if (opType == OpType.NOT) {
                curCode += String.format("seq $t0,$t0,$0");
            }
            curCode += "\n";
            store(dst,null);
        } else {
            String reg0;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
                reg0 = "$t0";
            } else {
                reg0 = load(r1);
            }
            curCode += "\n";
            if (opType == OpType.NEG) {
                curCode += String.format("neg %s,%s",reg0,reg0);
            } else if (opType == OpType.NOT) {
                curCode += String.format("seq %s,%s,$0",reg0,reg0);
            }
            curCode += "\n";
            store(dst,reg0);
        }
    }

    public boolean isDoubleOp() {
        if (opType == OpType.ADD || opType == OpType.SUB || opType == OpType.MULT || opType == OpType.DIV ||
            opType == OpType.MOD ||
            opType == OpType.SLT || opType == OpType.SLE || opType == OpType.SGT || opType == OpType.SGE ||
            opType == OpType.SEQ || opType == OpType.SNE) {
            return true;
        }
        return false;
    }

    public boolean isSingleOp() {
        if (opType == OpType.NEG || opType == OpType.NOT) {
            return true;
        }
        return false;
    }

    public void genLabel() {
        curCode += String.format("%s:",dst);
    }

    public void genBeqz() {
        //todo change t
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
            } else {
                load(r1);
            }
            curCode += "\n";
            curCode += String.format("beqz $t0,%s",dst);
        } else {
            String reg0;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
                reg0 = "$t0";
            } else {
                reg0 = load(r1);
            }
            curCode += "\n";
            curCode += String.format("beqz %s,%s",reg0,dst);
        }
    }

    public void genBnez() {
        if (!Optimizer.isOp()) {
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
            } else {
                load(r1);
            }
            curCode += "\n";
            curCode += String.format("bnez $t0,%s",dst);
        } else {
            String reg0;
            if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
                curCode += String.format("li $t0,%s",r1);
                reg0 = "$t0";
            } else {
                reg0 = load(r1);
            }
            curCode += "\n";
            curCode += String.format("bnez %s,%s",reg0,dst);
        }
    }

    public void genGoto() {
        curCode += String.format("j %s",dst);
    }

    //优化#########################################################
    //#########################################################
    //#########################################################

    public boolean isEntryPoint() {
        return isEntryPoint;
    }

    public void setEntryPoint(boolean entryPoint) {
        isEntryPoint = entryPoint;
    }

    public String getR1() {
        return r1;
    }

    public void setR1(String r1) {
        this.r1 = r1;
    }

    public void setR2(String r2) {
        this.r2 = r2;
    }

    public void setR3(String r3) {
        this.r3 = r3;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }

    public String getR2() {
        return r2;
    }

    public String getR3() {
        return r3;
    }

    public String getDst() {
        return dst;
    }

    public void genGenKill(FuncBlock funcBlock) {
        for (BasicBlock basicBlock: funcBlock.getBasicBlocks()) {
            ArrayList<MidCodeEntry> midCodeEntries = basicBlock.getMidCodeList();
            for (int i = 0;i < midCodeEntries.size();i++) {
                MidCodeEntry midCodeEntry = midCodeEntries.get(i);
                if (this.equals(midCodeEntry)) {
                    gen = new DefPoint(r1,basicBlock.getBlockNum(),i);
                } else if (midCodeEntry.getOpType() == OpType.ASSIGN && r1.equals(midCodeEntry.getR1())) {
                    DefPoint defPoint = new DefPoint(r1,basicBlock.getBlockNum(),i);
                    kill.add(defPoint);
                }
            }
        }
    }

    public HashSet<DefPoint> getKill() {
        return kill;
    }

    public DefPoint getGen() {
        return gen;
    }

    public void setFatherBlock(BasicBlock basicBlock) {
        fatherBlock = basicBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return false;
    }
}
