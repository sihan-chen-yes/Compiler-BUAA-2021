package MidCodeGeneration;
import Enum.OpType;
import WordAnalysis.Word;

public class MidCodeEntry {
    private int line;
    private OpType opType;
    private Word r1;
    private Word r2;
    private Word dst;

    public MidCodeEntry(OpType opType,Word r1,Word r2,Word dst) {
        this.opType = opType;
        this.r1 = r1;
        this.r2 = r2;
        this.dst = dst;
    }
    @Override
    public String toString() {
        String midCode = null;
        if (opType == OpType.GLOBAL_DECLARE) {
            midCode = String.format("GLOBAL_DECLARE %s", dst.getWord());
        } else if (opType == OpType.FUNC_DECLARE) {
            midCode = String.format("FUNC_DECLARE %s", dst.getWord());
        } else if (opType == OpType.SET_ARG) {
            midCode = String.format("SET_ARG %s", dst.getWord());
        } else if (opType == OpType.LOAD_ARRAY) {
            midCode = String.format("LOAD_ARRAY %s %s %s", dst.getWord(), r1.getWord(), r2.getWord());
        } else if (opType == OpType.STORE_ARRAY) {
            midCode = String.format("STORE_ARRAY %s %s %s", dst.getWord(), r1.getWord(), r2.getWord());
        } else if (opType == OpType.ASSIGN) {
            midCode = String.format("ASSIGN %s %s", dst.getWord(), r1.getWord());
        } else if (opType == OpType.PRINT) {
            midCode = String.format("PRINT %s", dst.getWord());
        } else if (opType == OpType.RET_VALUE) {
            midCode = String.format("RET_VALUE %s", dst.getWord());
        } else if (opType == OpType.RET_VOID) {
            midCode = String.format("RET_VOID");
        } else if (opType == OpType.GETINT) {
            midCode = String.format("GETINT %s", dst.getWord());
        } else if (opType == OpType.PREPARE_CALL) {
            midCode = String.format("PREPARE %s", dst.getWord());
        } else if (opType == OpType.CALL) {
            midCode = String.format("CALL %s", dst.getWord());
        } else if (opType == OpType.FIN_CALL) {
            midCode = String.format("FIN_CALL %s", dst.getWord());
        } else if (opType == OpType.ADD) {
            midCode = String.format("ADD %s %s %s", r1.getWord(), r2.getWord(), dst.getWord());
        } else if (opType == OpType.SUB) {
            midCode = String.format("SUB %s %s %s", r1.getWord(), r2.getWord(), dst.getWord());
        } else if (opType == OpType.MULT) {
            midCode = String.format("MULT %s %s %s", r1.getWord(), r2.getWord(), dst.getWord());
        } else if (opType == OpType.DIV) {
            midCode = String.format("DIV %s %s %s", r1.getWord(), r2.getWord(), dst.getWord());
        } else if (opType == OpType.MOD) {
            midCode = String.format("MOD %s %s %s", r1.getWord(), r2.getWord(), dst.getWord());
        }
        return midCode;
        //Todo
    }

    public String toTargetCode() {
        //Todo
        return "";
    }
}
