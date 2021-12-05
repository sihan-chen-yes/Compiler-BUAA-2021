package MidCodeGeneration;
import Enum.*;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;

import java.util.ArrayList;

public class MidCodeEntry {
    private OpType opType;
    private String r1;
    private String r2;
    private String r3;
    private String dst;

    private boolean isEntryPoint = false;

    private String funcStart = "########################################FUNC START##########################################\n";
    private String funcEnd = "\n########################################FUNC END############################################";

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
            midCode = funcStart + String.format("FUNC_DECLARE %s", dst);
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
            midCode = String.format("RET_VALUE %s", dst) + funcEnd;
        } else if (opType == OpType.RET_VOID) {
            midCode = String.format("RET_VOID") + funcEnd;
        } else if (opType == OpType.EXIT) {
            midCode = String.format("EXIT") + funcEnd;
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
        String tarCode = start;
        if (opType == OpType.GLOBAL_DECLARE) {
            tarCode += genStoreGlobal();
        }else if (opType == OpType.FUNC_DECLARE) {
            MidCodeGener.startFuncDef(dst);
            tarCode = funcStart + String.format("#############FUNC_DECLARE %s#############\n", dst);
            tarCode += genFuncLabel();
        } else if (opType == OpType.PUSH_PARAM) {
            tarCode += genPushParam();
        } else if (opType == OpType.STORE_RET) {
            tarCode += genStoreRet();
        } else if (opType == OpType.LOAD_ARRAY_1D) {
            tarCode += genLoadArray1D();
        } else if (opType == OpType.STORE_ARRAY_1D) {
            tarCode += genStoreArray1D();
        } else if (opType == OpType.LOAD_ARRAY_2D) {
            tarCode += genLoadArray2D();
        } else if (opType == OpType.STORE_ARRAY_2D) {
            tarCode += genStoreArray2D();
        } else if (opType == OpType.LOAD_ARRDESS) {
            tarCode += genLoadAddress();
        } else if (opType == OpType.ASSIGN) {
            tarCode += genAssign();
        } else if (opType == OpType.PRINT_STRING) {
            tarCode += genPrintStr();
        } else if (opType == OpType.PRINT_INT) {
            tarCode += genPrintInt();
        } else if (opType == OpType.RET_VALUE) {
            tarCode = String.format("#############RET_VALUE %s#############\n", dst) + genRetValue() + funcEnd;
        } else if (opType == OpType.RET_VOID) {
            tarCode = String.format("#############RET_VOID#############\n") + genRetVoid() + funcEnd;
        } else if (opType == OpType.EXIT) {
            tarCode = String.format("#############EXIT#############\n") + genExit() + funcEnd;
        } else if (opType == OpType.GETINT) {
            tarCode += genGetInt();
        } else if (opType == OpType.PREPARE_CALL) {
            tarCode += genPrePareCall();
        } else if (opType == OpType.CALL) {
            tarCode += genCall();
        } else if (opType == OpType.FIN_CALL) {
            tarCode += genFinCall();
        } else if (opType == OpType.ADD) {
            tarCode += genAdd();
        } else if (opType == OpType.SUB) {
            tarCode += genSub();
        } else if (opType == OpType.MULT) {
            tarCode += genMult();
        } else if (opType == OpType.DIV) {
            tarCode = genDiv();
        } else if (opType == OpType.MOD) {
            tarCode += genMod();
        } else if (opType == OpType.NEG) {
            tarCode += genNeg();
        } else if (opType == OpType.SLT) {
            tarCode += genSlt();
        } else if (opType == OpType.SGT) {
            tarCode += genSgt();
        } else if (opType == OpType.SLE) {
            tarCode += genSle();
        } else if (opType == OpType.SGE) {
            tarCode += genSge();
        } else if (opType == OpType.SEQ) {
            tarCode += genSeq();
        } else if (opType == OpType.SNE) {
            tarCode += genSne();
        } else if (opType == OpType.NOT) {
            tarCode += genNot();
        } else if (opType == OpType.LABEL_GEN) {
            tarCode += genLabel();
        } else if (opType == OpType.BEQZ) {
            tarCode += genBeqz();
        } else if (opType == OpType.BNEZ) {
            tarCode += genBnez();
        } else {
            assert opType == OpType.GOTO;
            tarCode += genGoto();
        }
        tarCode += end;
        return tarCode;
    }

    public String genStoreGlobal() {
        String tarCode = String.format("%s:.word ",dst);
        SymbolTableEntry symbolTableEntry = MidCodeGener.getSymbolTable().search_global(dst);
        ArrayList<Integer> flattenValues = symbolTableEntry.getFlattenValues();
        tarCode += flattenValues.get(0).toString();
        for (int i = 1;i < flattenValues.size();i++) {
            tarCode += String.format(",%s",flattenValues.get(i).toString());
        }
        return tarCode;
    }

    public String genFuncLabel() {
        return String.format("%s:",dst);
    }

    public String loadParam() {
        //Todo 分配其他reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        //现在位于的函数体
        int size = symbolTable.getLocalSize(dst);
        //要调用的函数体的大小
        int arg_offset = size - (Integer.parseInt(r2) + 1) * 4;
        //压入参数的位置
        if (symbolTable.isNumber(func,r1)) {
            //是常数
            tarCode = String.format("li $t0,%s",r1);
            tarCode += "\n";
            tarCode += String.format("sw $t0,%d($sp)",arg_offset);
        } else {
            //是局部变量
            if (symbolTable.isLocal(func,r1)) {
                int offset = symbolTable.searchOffset_sp(func,r1);
                //相对于当前sp的offset
                tarCode = String.format("lw $t0,%d($sp)",offset);
                tarCode += "\n";
                tarCode += String.format("sw $t0,%d($sp)",arg_offset);
            } else {
                assert symbolTable.isGlobal(r1);
                int offset = symbolTable.searchOffset_gp(r1);
                tarCode = String.format("lw $t0,%d($gp)",offset);
                tarCode += "\n";
                tarCode += String.format("sw $t0,%d($sp)",arg_offset);
            }
        }
        return tarCode;
    }

    public String genPushParam() {
        //只可能是常数或者局部变量 或者全局变量
        String tarCode;
        tarCode = loadParam();
        //Todo 寄存器分配
        assert tarCode != null;
        return tarCode;
    }

    public String genStoreRet() {
        //默认返回值在v0
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int offset = symbolTable.searchOffset_sp(func,dst);
        String tarCode = String.format("sw $v0,%d($sp)",offset);
        //Todo 寄存器分配
        return tarCode;
    }

    public String load(String name) {
        //将四元式中的Ident或者常数load到t0
        //Todo 分配其他reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isNumber(func,name)) {
            tarCode = String.format("li $t0,%d",Integer.parseInt(name));
        } else {
            if (symbolTable.isLocal(func,name)) {
                int offset = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("lw $t0,%d($sp)",offset);
            } else {
                assert symbolTable.isGlobal(name);
                int offset = symbolTable.searchOffset_gp(name);
                tarCode = String.format("lw $t0,%d($gp)",offset);
            }
        }
        return tarCode;
    }

    public String store(String name) {
        //将t0存到对应的Ident中
        //Todo 分配其他reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isLocal(func,name)) {
            int offset = symbolTable.searchOffset_sp(func,name);
            tarCode = String.format("sw $t0,%d($sp)",offset);
        } else {
            assert symbolTable.isGlobal(name);
            int offset = symbolTable.searchOffset_gp(name);
            tarCode = String.format("sw $t0,%d($gp)",offset);
        }
        return tarCode;
    }

    public String calOffset1D(String i) {
        // i is T 算出绝对地址 存到t0
        String tarCode;
        tarCode = loadSec(i);
        tarCode += "\n";
        tarCode += String.format("sll $t1,$t1,2");
        tarCode += "\n";
        tarCode += String.format("addu $t0,$t0,$t1");
        return tarCode;
    }

    public String calOffset2D(String i, String j, int length2D) {
        //i j is T t1 t2 算出绝对地址放到t0
        String tarCode;
        tarCode = loadSec(i);
        tarCode += "\n";
        tarCode += String.format("li $t2,%d",length2D);
        tarCode += "\n";
        tarCode += String.format("mult $t1,$t2");
        tarCode += "\n";
        tarCode += String.format("mflo $t1");
        tarCode += "\n";
        tarCode += loadThird(j);
        tarCode += "\n";
        tarCode += String.format("addu $t1,$t1,$t2");
        tarCode += "\n";
        tarCode += String.format("sll $t1,$t1,2");
        tarCode += "\n";
        tarCode += String.format("addu $t0,$t0,$t1");
        return tarCode;
    }

    public String calOffsetAddr(String i,int length2D) {
        String tarCode;
        tarCode = loadSec(i);
        //$t1
        tarCode += "\n";
        tarCode += String.format("li $t2,%d",length2D);
        tarCode += "\n";
        tarCode += String.format("mult $t1,$t2");
        tarCode += "\n";
        tarCode += String.format("mflo $t1");
        tarCode += "\n";
        tarCode += String.format("sll $t1,$t1,2");
        tarCode += "\n";
        tarCode += String.format("addu $t0,$t0,$t1");
        return tarCode;
    }

    public String load1D(String name,String i) {
        //Todo 分配其他reg 常量传播可优化
        //可能是全局或者局部 加载到t0 后续优化reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isLocal(func,name)) {
            if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                //参数（存的是地址）
                tarCode = load(name);
                tarCode += "\n";
                tarCode += calOffset1D(i);
                tarCode += "\n";
                tarCode += String.format("lw $t0,0($t0)");
            } else {
                int offset_sp = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("addiu $t0,$sp,%d",offset_sp);
                tarCode += "\n";
                tarCode += calOffset1D(i);
                tarCode += "\n";
                tarCode += String.format("lw $t0,0($t0)");
            }
        } else {
            assert symbolTable.isGlobal(name);
            int offset_gp = symbolTable.searchOffset_gp(name);
            tarCode = String.format("addiu $t0,$gp,%d",offset_gp);
            tarCode += "\n";
            tarCode += calOffset1D(i);
            tarCode += "\n";
            tarCode += String.format("lw $t0,0($t0)");
        }
        return tarCode;
    }

    public String load2D(String name,String i,String j) {
        //Todo 常量传播可优化
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int length2D;
        if (symbolTable.isLocal(func,name)) {
            length2D = symbolTable.search_local(func,name).getLength2D();
            if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                tarCode = load(name);
                tarCode += "\n";
                tarCode += calOffset2D(i,j,length2D);
                tarCode += "\n";
                tarCode += String.format("lw $t0,0($t0)");
            } else {
                int offset_sp = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("addiu $t0,$sp,%d",offset_sp);
                tarCode += "\n";
                tarCode += calOffset2D(i,j,length2D);
                tarCode += "\n";
                tarCode += String.format("lw $t0,0($t0)");
            }
        } else {
            assert symbolTable.isGlobal(name);
            length2D = symbolTable.search_global(name).getLength2D();
            int offset_gp = symbolTable.searchOffset_gp(name);
            tarCode = String.format("addiu $t0,$gp,%d",offset_gp);
            tarCode += "\n";
            tarCode += calOffset2D(i,j,length2D);
            tarCode += "\n";
            tarCode += String.format("lw $t0,0($t0)");
        }
        return tarCode;
    }

    public String store1D(String name,String i) {
        //Todo 常量传播可优化
        //store时t0 已被用 后续优化reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isLocal(func,name)) {
            if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                //参数（存的是地址）
                tarCode = String.format("move $t3,$t0");
                //$t0此时已经用了
                tarCode += "\n";
                tarCode += load(name);
                tarCode += "\n";
                tarCode += calOffset1D(i);
                tarCode += "\n";
                tarCode += String.format("sw $t3,0($t0)");
            } else {
                int offset_sp = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("move $t3,$t0");
                tarCode += "\n";
                tarCode += String.format("addiu $t0,$sp,%d",offset_sp);
                tarCode += "\n";
                tarCode += calOffset1D(i);
                tarCode += "\n";
                tarCode += String.format("sw $t3,0($t0)");
            }
        } else {
            assert symbolTable.isGlobal(name);
            int offset_gp = symbolTable.searchOffset_gp(name);
            tarCode = String.format("move $t3,$t0");
            tarCode += "\n";
            tarCode += String.format("addiu $t0,$gp,%d",offset_gp);
            tarCode += "\n";
            tarCode += calOffset1D(i);
            tarCode += "\n";
            tarCode += String.format("sw $t3,0($t0)");
        }
        return tarCode;
    }

    public String store2D(String name,String i,String j) {
        //Todo
        //store时t0 已被用 后续优化reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        int length2D;
        if (symbolTable.isLocal(func,name)) {
            length2D = symbolTable.search_local(func,name).getLength2D();
            if (symbolTable.search_local(func,name).getDeclType() == DeclType.PARAM) {
                tarCode = String.format("move $t3,$t0");
                tarCode += "\n";
                tarCode += load(name);//得到地址
                tarCode += "\n";
                tarCode += calOffset2D(i,j,length2D);
                tarCode += "\n";
                tarCode += String.format("sw $t3,0($t0)");
            } else {
                int offset_sp = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("move $t3,$t0");
                tarCode += "\n";
                tarCode += String.format("addiu $t0,$sp,%d",offset_sp);
                tarCode += "\n";
                tarCode += calOffset2D(i,j,length2D);
                tarCode += "\n";
                tarCode += String.format("sw $t3,0($t0)");
            }
        } else {
            assert symbolTable.isGlobal(name);
            length2D = symbolTable.search_global(name).getLength2D();
            int offset_gp = symbolTable.searchOffset_gp(name);
            tarCode = String.format("move $t3,$t0");
            tarCode += "\n";
            tarCode += String.format("addiu $t0,$gp,%d",offset_gp);
            tarCode += "\n";
            tarCode += calOffset2D(i,j,length2D);
            tarCode += "\n";
            tarCode += String.format("sw $t3,0($t0)");

        }
        return tarCode;
    }

    public String genLoadArray1D() {
        String tarCode;
        tarCode = load1D(r1,r2);
        tarCode += "\n";
        tarCode += store(dst);
        //Todo 可以优化
        return tarCode;
    }

    public String genStoreArray1D() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
            tarCode = String.format("li $t0,%s",dst);
        } else {
            tarCode = load(dst);
        }
        tarCode += "\n";
        tarCode += store1D(r1,r2);
        return tarCode;
    }

    public String genLoadArray2D() {
        String tarCode;
        tarCode = load2D(r1,r2,r3);
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genStoreArray2D() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
            tarCode = String.format("li $t0,%s",dst);
        } else {
            tarCode = load(dst);
        }
        tarCode += "\n";
        tarCode += store2D(r1,r2,r3);
        return tarCode;
    }

    public String genLoadAddress() {
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (r2 == null) {
            if (symbolTable.isLocal(func,r1)) {
                if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                    tarCode = load(r1);
                    tarCode += "\n";
                    tarCode += store(dst);
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,r1);
                    tarCode = String.format("addiu $t0,$sp,%d",offset_sp);
                    //算出地址
                    tarCode += "\n";
                    tarCode += store(dst);
                }
            } else {
                assert symbolTable.isGlobal(r1);
                int offset_gp = symbolTable.searchOffset_gp(r1);
                tarCode = String.format("addiu $t0,$gp,%d",offset_gp);
                //算出地址
                tarCode += "\n";
                tarCode += store(dst);
            }
        } else {
            if (symbolTable.isLocal(func,r1)) {
                //Todo
                if (symbolTable.search_local(func,r1).getDeclType() == DeclType.PARAM) {
                    tarCode = load(r1);
                    //参数中的地址 放在t0
                    tarCode += "\n";
                    int length2D = symbolTable.search_local(func,r1).getLength2D();
                    tarCode += calOffsetAddr(r2,length2D);
                    //$t0就是地址
                    tarCode += "\n";
                    tarCode += store(dst);
                } else {
                    int offset_sp = symbolTable.searchOffset_sp(func,r1);
                    tarCode = String.format("addiu $t0,$sp,%d",offset_sp);
                    int length2D = symbolTable.search_local(func,r1).getLength2D();
                    tarCode += "\n";
                    tarCode += calOffsetAddr(r2,length2D);
                    //算出地址
                    tarCode += "\n";
                    tarCode += store(dst);
                }
            } else {
                assert symbolTable.isGlobal(r1);
                int offset_gp = symbolTable.searchOffset_gp(r1);
                tarCode = String.format("addiu $t0,$gp,%d",offset_gp);
                int length2D = symbolTable.search_global(r1).getLength2D();
                tarCode += "\n";
                tarCode += calOffsetAddr(r2,length2D);
                //算出地址
                tarCode += "\n";
                tarCode += store(dst);
            }
        }
        return tarCode;
    }

    public String genAssign() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
            tarCode = String.format("li $t0,%s",dst);
        } else {
            tarCode = load(dst);
        }
        tarCode += "\n";
        tarCode += store(r1);
        return tarCode;
    }

    public String genPrintStr() {
        //Todo 寄存器冲突
        String tarCode;
        tarCode = String.format("la $a0,%s",dst);
        tarCode += "\n";
        tarCode += String.format("li $v0,4");
        tarCode += "\n";
        tarCode += String.format("syscall");
        return tarCode;
    }

    public String genPrintInt() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
            tarCode = String.format("li $t0,%s",dst);
        } else {
            tarCode = load(dst);
        }
        tarCode += "\n";
        tarCode += String.format("move $a0,$t0");
        tarCode += "\n";
        tarCode += String.format("li $v0,1");
        tarCode += "\n";
        tarCode += String.format("syscall");
        return tarCode;
    }

    public String genRetValue() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),dst)) {
            tarCode = String.format("li $t0,%s",dst);
        } else {
            tarCode = load(dst);
        }
        tarCode += "\n";
        tarCode += String.format("move $v0,$t0");
        tarCode += "\n";
        tarCode += String.format("jr $ra");
        return tarCode;
    }

    public String genRetVoid() {
        String tarCode;
        tarCode = String.format("jr $ra");
        return tarCode;
    }

    public String genGetInt() {
        String tarCode;
        tarCode = String.format("li $v0,5");
        tarCode += "\n";
        tarCode += String.format("syscall");
        tarCode += "\n";
        tarCode += String.format("move $t0,$v0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genPrePareCall() {
        String tarCode;
        int size = MidCodeGener.getSymbolTable().getLocalSize(dst);
        tarCode = String.format("sw $ra,0($sp)");
        tarCode += "\n";
        tarCode += String.format("addiu $sp,$sp,-%d",size);
        MidCodeGener.getSymbolTable().addStack_size(size);
        //Todo save s t a
        return tarCode;
    }

    public String genCall() {
        String tarCode;
        tarCode = String.format("jal %s",dst);
        return tarCode;
    }

    public String genFinCall() {
        //Todo 恢复 s t a
        int size = MidCodeGener.getSymbolTable().getLocalSize(dst);
        String tarCode;
        tarCode = String.format("addiu $sp,$sp,%d",size);
        tarCode += "\n";
        tarCode += String.format("lw $ra,0($sp)");
        MidCodeGener.getSymbolTable().subStack_size(size);
        return tarCode;
    }

    public String genExit() {
        String tarCode;
        tarCode = String.format("li $v0,10");
        tarCode += "\n";
        tarCode += String.format("syscall");
        return tarCode;
    }

    public String loadSec(String name) {
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isNumber(func,name)) {
            tarCode = String.format("li $t1,%d",Integer.parseInt(name));
        } else {
            if (symbolTable.isLocal(func,name)) {
                int offset = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("lw $t1,%d($sp)",offset);
            } else {
                assert symbolTable.isGlobal(name);
                int offset = symbolTable.searchOffset_gp(name);
                tarCode = String.format("lw $t1,%d($gp)",offset);
            }
        }
        return tarCode;
    }

    public String loadThird(String name) {
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isNumber(func,name)) {
            tarCode = String.format("li $t2,%d",Integer.parseInt(name));
        } else {
            if (symbolTable.isLocal(func,name)) {
                int offset = symbolTable.searchOffset_sp(func,name);
                tarCode = String.format("lw $t2,%d($sp)",offset);
            } else {
                assert symbolTable.isGlobal(name);
                int offset = symbolTable.searchOffset_gp(name);
                tarCode = String.format("lw $t2,%d($gp)",offset);
            }
        }
        return tarCode;
    }

    public String genAdd() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("addu $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSub() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("subu $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genMult() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("mult $t0,$t1");
        tarCode += "\n";
        tarCode += String.format("mflo $t0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genDiv() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("div $t0,$t1");
        tarCode += "\n";
        tarCode += String.format("mflo $t0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genMod() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("div $t0,$t1");
        tarCode += "\n";
        tarCode += String.format("mfhi $t0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genNeg() {
        String tarCode;
        //Todo 此处可优化
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        tarCode += String.format("neg $t0,$t0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSlt() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("slt $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSgt() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("sgt $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSge() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("slt $t0,$t0,$t1");
        tarCode += "\n";
        //取反
        tarCode += String.format("seq $t0,$t0,$0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSle() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("sgt $t0,$t0,$t1");
        tarCode += "\n";
        //取反
        tarCode += String.format("seq $t0,$t0,$0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSne() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("sne $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genSeq() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r2)) {
            tarCode += String.format("li $t1,%s",r2);
        } else {
            tarCode += loadSec(r2);
        }
        tarCode += "\n";
        tarCode += String.format("seq $t0,$t0,$t1");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genNot() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        tarCode += String.format("seq $t0,$t0,$0");
        tarCode += "\n";
        tarCode += store(dst);
        return tarCode;
    }

    public String genLabel() {
        return String.format("%s:",dst);
    }

    public String genBeqz() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        tarCode += String.format("beqz $t0,%s",dst);
        return tarCode;
    }

    public String genBnez() {
        String tarCode;
        if (MidCodeGener.getSymbolTable().isNumber(MidCodeGener.getFuncName(),r1)) {
            tarCode = String.format("li $t0,%s",r1);
        } else {
            tarCode = load(r1);
        }
        tarCode += "\n";
        tarCode += String.format("bnez $t0,%s",dst);
        return tarCode;
    }

    public String genGoto() {
        return String.format("j %s",dst);
    }

    //优化#########################################################

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
}
