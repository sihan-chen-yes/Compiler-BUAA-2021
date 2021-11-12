package MidCodeGeneration;
import Enum.*;

public class MidCodeEntry {
    private OpType opType;
    private String r1;
    private String r2;
    private String r3;
    private String dst;

    private String funcStart = "########################################FUNC START########################################\n";
    private String funcEnd = "\n########################################FUNC END########################################";

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
        //Todo
        return "";
    }
}
