package MidCodeGeneration;
import Enum.OpType;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;

import java.util.ArrayList;

public class MidCodeEntry {
    private OpType opType;
    private String r1;
    private String r2;
    private String r3;
    private String dst;

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
            midCode = String.format("LOAD_ADDRESS %s %s %s",r1,r2,dst);
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
        }
        return midCode;
        //Todo 其他中间代码
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
            tarCode = String.format("STORE_ARRAY_1D %s %s %s", r1,r2,dst);
        } else if (opType == OpType.LOAD_ARRAY_2D) {
            tarCode = String.format("LOAD_ARRAY_2D %s %s %s %s", r1,r2,r3,dst);
        } else if (opType == OpType.STORE_ARRAY_2D) {
            tarCode = String.format("STORE_ARRAY_2D %s %s %s %s", r1,r2,r3,dst);
        } else if (opType == OpType.LOAD_ARRDESS) {
            if (r2 == null) {
                tarCode = String.format("LOAD_ADDRESS %s %s",r1,dst);
            } else {
                tarCode = String.format("LOAD_ADDRESS %s %s %s",r1,r2,dst);
            }
        } else if (opType == OpType.ASSIGN) {
            tarCode = String.format("ASSIGN %s %s", r1,dst);
        } else if (opType == OpType.PRINT_STRING) {
            tarCode = String.format("PRINT_STRING %s", dst);
        } else if (opType == OpType.PRINT_INT) {
            tarCode = String.format("PRINT_INT %s",dst);
        } else if (opType == OpType.RET_VALUE) {
            tarCode = String.format("RET_VALUE %s", dst) + funcEnd;
        } else if (opType == OpType.RET_VOID) {
            tarCode = String.format("RET_VOID") + funcEnd;
        } else if (opType == OpType.EXIT) {
            tarCode = String.format("EXIT") + funcEnd;
        } else if (opType == OpType.GETINT) {
            tarCode = String.format("GETINT %s", dst);
        } else if (opType == OpType.PREPARE_CALL) {
            tarCode += genPrePareCall();
        } else if (opType == OpType.CALL) {
            tarCode = String.format("CALL %s", dst);
        } else if (opType == OpType.FIN_CALL) {
            tarCode = String.format("FIN_CALL %s", dst);
        } else if (opType == OpType.ADD) {
            tarCode = String.format("ADD %s %s %s", r1,r2,dst);
        } else if (opType == OpType.SUB) {
            tarCode = String.format("SUB %s %s %s", r1,r2,dst);
        } else if (opType == OpType.MULT) {
            tarCode = String.format("MULT %s %s %s", r1,r2,dst);
        } else if (opType == OpType.DIV) {
            tarCode = String.format("DIV %s %s %s", r1,r2,dst);
        } else if (opType == OpType.MOD) {
            tarCode = String.format("MOD %s %s %s", r1,r2,dst);
        } else if (opType == OpType.NEG) {
            tarCode = String.format("NEG %s %s",r1,dst);
        }
        tarCode += end;
        return tarCode;
        //Todo 其他目标代码
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

    public String genPushParam() {
        //只可能是常数或者局部变量或者全局变量
        String tarCode;
        String func = MidCodeGener.getFuncName();
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        if (symbolTable.isLocal(func,r1)) {
            tarCode = String.format("lw $t0,%d($sp)",symbolTable.searchOffset_sp(func,r1));
            tarCode += "\n";
            tarCode += String.format("sw $t0,-%d($sp)",(Integer.parseInt(r2) + 1) * 4);
        } else if (symbolTable.isGlobal(r1)) {
            tarCode = String.format("lw $t0,%d($gp)",symbolTable.searchOffset_gp(r1));
            tarCode += "\n";
            tarCode += String.format("sw $t0,-%d($sp)",(Integer.parseInt(r2) + 1) * 4);
        } else {
            assert symbolTable.isNumber(MidCodeGener.getFuncName(),r1);
            tarCode = String.format("sw %s,-%d($sp)",r1,(Integer.parseInt(r2) + 1) * 4);
        }
        //Todo 寄存器分配
        assert tarCode != null;
        return tarCode;
    }

    public String genStoreRet() {
        //默认返回值在只在v0
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        String tarCode = String.format("sw $v0,%d($sp)",symbolTable.searchOffset_sp(func,dst));
        //Todo 寄存器分配
        return tarCode;
    }

    public String load(String name) {

    }

    public String load1D(String name,int i) {
        //可能是全局或者局部 加载到t0 后续优化reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isLocal(func,name)) {
            int offset = symbolTable.searchOffset_sp(func,name);
            offset -= i * 4;
            tarCode = String.format("lw $t0,%d($sp)",offset);
        } else {
            assert symbolTable.isGlobal(name);
            int offset = symbolTable.searchOffset_gp(name);
            offset += i * 4;
            tarCode = String.format("lw $t0,%d($gp)",offset);
        }
        return tarCode;
    }

    public String store1D(String name,int i) {
        //先都用t0 后续优化reg
        String tarCode;
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        String func = MidCodeGener.getFuncName();
        if (symbolTable.isLocal(func,name)) {
            int offset = symbolTable.searchOffset_sp(func,name);
            offset -= i * 4;
            tarCode = String.format("sw $t0,%d($sp)",offset);
        } else {
            assert symbolTable.isGlobal(name);
            int offset = symbolTable.searchOffset_gp(name);
            offset += i * 4;
            tarCode = String.format("sw $t0,%d($gp)",offset);
        }
        return tarCode;
    }

    public String genLoadArray1D() {
        String tarCode;
        tarCode = load1D(r1,Integer.parseInt(r1)) + "\n" + store1D(dst,)
        tarCode +=
        //Todo 可以优化
    }

//    public String genStoreArray1D() {
//
//    }
//
//    public String genLoadArray2D() {
//
//    }
//
//    public String genStoreArray2D() {
//
//    }
//
//    public String genLoadAddress() {
//
//    }
//
//    public String genAssign() {
//
//    }
//
//    public String genPrintStr() {
//
//    }
//
//    public String genPrintInt() {
//
//    }
//
//    public String genRetValue() {
//
//    }
//
//    public String genRetVoid() {
//
//    }
//
//    public String genGetInt() {
//
//    }

    public String genPrePareCall() {
        String tarCode = String.format("sw $ra,0($sp)");
        //Todo save s t a
        return tarCode;
    }

//    public String genCall() {
//
//    }
//
//    public String genFinCall() {
//
//    }
//
//    public String genExit() {
//
//    }
//
//    public String genAdd() {
//
//
//    }
//
//    public String genSub() {
//
//    }
//
//    public String genMult() {
//
//    }
//
//    public String genDiv() {
//
//    }
//
//    public String genMod() {
//
//    }
//
//    public String genNeg() {
//
//    }


    //Todo  其他目标代码
}
