package Optimizer;

import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;

public class Optimizer {
    private ArrayList<MidCodeEntry> midCodeList;
    private ArrayList<MidCodeEntry> optimizedMidCode = null;

    private ArrayList<FuncBlock> funcBlocks = new ArrayList<>();
    private FuncBlock curFuncBlock = null;
    private BasicBlock curBasicBlock = null;

    private int blockNum = 0;

    private static boolean isOp = false;
    public Optimizer() {
        this.midCodeList = MidCodeGener.getMidCodeList();
    }

    public static boolean isOp() {
        return isOp;
    }

    public static void setOp(boolean isOp) {
        Optimizer.isOp = isOp;
    }

    public void optimize() {
        genBlock();
//        genDAG();
//        MidCodeGener.setMidCodeList(getOptimizedMidCode());
    }

    public void findEntry() {
        for (int i = 0;i < midCodeList.size();i++) {
            MidCodeEntry midCodeEntry = midCodeList.get(i);
            if (midCodeEntry.getOpType() == OpType.LABEL_GEN) {
                midCodeEntry.setEntryPoint(true);
            } else if ((midCodeEntry.getOpType() == OpType.BEQZ
                    || midCodeEntry.getOpType() == OpType.BNEZ
                    || midCodeEntry.getOpType() == OpType.GOTO
                    || midCodeEntry.getOpType() == OpType.RET_VOID
                    || midCodeEntry.getOpType() == OpType.RET_VALUE
                    || midCodeEntry.getOpType() == OpType.EXIT)
                    && i + 1 < midCodeList.size())   {
                midCodeList.get(i + 1).setEntryPoint(true);
            }
        }
    }

    public void genBlock() {
        findEntry();
        for (int i = 0;i < midCodeList.size();i++) {
            MidCodeEntry midCodeEntry = midCodeList.get(i);
            if (midCodeEntry.getOpType() == OpType.FUNC_DECLARE) {
                curFuncBlock = new FuncBlock(midCodeEntry);
                funcBlocks.add(curFuncBlock);
                //创建新的func块
                if (i + 1 < midCodeList.size() && midCodeList.get(i + 1).getOpType() != OpType.FUNC_DECLARE) {
                    midCodeList.get(i + 1).setEntryPoint(true);
                }
            } else if (midCodeEntry.isEntryPoint()) {
                curBasicBlock = new BasicBlock();
                curBasicBlock.setNum(blockNum++);
                curFuncBlock.addBasicBlock(curBasicBlock);
                curBasicBlock.addMideCodeEntry(midCodeEntry);
            } else {
                curBasicBlock.addMideCodeEntry(midCodeEntry);
            }
        }
    }

    public void genDAG() {
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.genDAG();
        }
        getOptimizedMidCode();
    }

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        if (optimizedMidCode == null) {
            optimizedMidCode = new ArrayList<>();
            for (FuncBlock funcBlock:funcBlocks) {
                optimizedMidCode.addAll(funcBlock.getOptimizedMidCode());
            }
        }
        return optimizedMidCode;
    }
}
