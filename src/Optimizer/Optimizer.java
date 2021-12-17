package Optimizer;

import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Optimizer {
    private ArrayList<MidCodeEntry> midCodeList;
    private ArrayList<MidCodeEntry> optimizedMidCode = null;

    private ArrayList<FuncBlock> funcBlocks = new ArrayList<>();
    private FuncBlock curFuncBlock = null;
    private BasicBlock curBasicBlock = null;
    private HashMap<String, HashMap<String,Integer>> funcToLabel = new HashMap<>();

    private int blockNum = 0;

    private static boolean isOp = false;
    private static boolean isDebug = false;

    public Optimizer() {
        this.midCodeList = MidCodeGener.getMidCodeList();
    }

    public static boolean isOp() {
        return isOp;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        Optimizer.isDebug = isDebug;
    }

    public static void setOp(boolean isOp) {
        Optimizer.isOp = isOp;
    }


    public void optimize() {
        genBlock();
        prune();
//        print();
        spread();
        genDataFlow();
        delDeadCode();
        dye();
        delWhile();
        MidCodeGener.setMidCodeList(getOptimizedMidCode());
    }

    public void prune() {
        for (FuncBlock funcBlock:funcBlocks) {
            ArrayList<BasicBlock> basicBlocks = funcBlock.getBasicBlocks();
            Iterator<BasicBlock> iterator = basicBlocks.iterator();
            while (iterator.hasNext()) {
                BasicBlock basicBlock = iterator.next();
                if (basicBlock.getPreBlocks().size() == 0) {
                    iterator.remove();
                }
            }
            for (int i = 0;i < basicBlocks.size();i++) {
                basicBlocks.get(i).setBlockNum(i);
            }
        }
    }

    public void print() {
        for (FuncBlock funcBlock:funcBlocks) {
            System.out.println(funcBlock.toString());
        }
    }

    public void findEntry() {
        for (int i = 0;i < midCodeList.size();i++) {
            MidCodeEntry midCodeEntry = midCodeList.get(i);
            if (midCodeEntry.getOpType() == OpType.LABEL_GEN &&
                    (i - 1 < 0 || i - 1 >= 0 && midCodeList.get(i - 1).getOpType() != OpType.LABEL_GEN)) {
                //第一个label点
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
                //当前中间代码是FUNC DECLARE
                curFuncBlock = new FuncBlock(midCodeEntry);
                blockNum = 0;
                funcBlocks.add(curFuncBlock);
                //创建新的func块
                if (i + 1 < midCodeList.size() && midCodeList.get(i + 1).getOpType() != OpType.FUNC_DECLARE) {
                    //不是空函数 FUNC DECLARE 的下一个中间代码是entry点
                    midCodeList.get(i + 1).setEntryPoint(true);
                }
            } else if (midCodeEntry.isEntryPoint()) {
                curBasicBlock = new BasicBlock();
                curBasicBlock.setBlockNum(blockNum++);
                curFuncBlock.addBasicBlock(curBasicBlock);
                curBasicBlock.setFatherBlock(curFuncBlock);
                if (midCodeEntry.getOpType() != OpType.LABEL_GEN) {
                    curBasicBlock.addMideCodeEntry(midCodeEntry);
                } else {
                    //第一个label点
                    curBasicBlock.addLabel(midCodeEntry.getDst());
                    if (!funcToLabel.containsKey(curBasicBlock.getFunc())) {
                        funcToLabel.put(curBasicBlock.getFunc(),new HashMap<>());
                    }
                    funcToLabel.get(curBasicBlock.getFunc()).put(midCodeEntry.getDst(),curBasicBlock.getBlockNum());
                }
            } else if (midCodeEntry.getOpType() == OpType.LABEL_GEN) {
                //其他连续的label点
                curBasicBlock.addLabel(midCodeEntry.getDst());
                funcToLabel.get(curBasicBlock.getFunc()).put(midCodeEntry.getDst(),curBasicBlock.getBlockNum());
            } else {
                curBasicBlock.addMideCodeEntry(midCodeEntry);
            }
        }
        setRel();
    }

    public void setRel() {
        for (FuncBlock funcBlock:funcBlocks) {
            HashMap<String, Integer> labelToNum = funcToLabel.get(funcBlock.getFunc());
            HeadBlock headBlock = funcBlock.getHeadBlock();
            EndBlock endBlock = funcBlock.getEndBlock();
            ArrayList<BasicBlock> basicBlocks = funcBlock.getBasicBlocks();
            headBlock.link(basicBlocks.get(0));
            for (int i = 0;i < basicBlocks.size();i++) {
                //每次连尾部
                MidCodeEntry midCodeEntry = basicBlocks.get(i).getLastMidCodeEntry();
                OpType opType = midCodeEntry.getOpType();
                if (opType == OpType.GOTO) {
                    String label = midCodeEntry.getDst();
                    BasicBlock basicBlock = basicBlocks.get(labelToNum.get(label));
                    basicBlocks.get(i).link(basicBlock);
                } else if (opType == OpType.BNEZ || opType == OpType.BEQZ) {
                    assert i + 1 < basicBlocks.size();
                    String label = midCodeEntry.getDst();
                    BasicBlock basicBlock = basicBlocks.get(labelToNum.get(label));
                    basicBlocks.get(i).link(basicBlock);
                    basicBlocks.get(i).link(basicBlocks.get(i + 1));
                } else if (opType == OpType.EXIT || opType == OpType.RET_VOID || opType == OpType.RET_VALUE) {
                    basicBlocks.get(i).link(endBlock);
                } else {
                    assert i + 1 < basicBlocks.size();
                    basicBlocks.get(i).link(basicBlocks.get(i + 1));
                }
            }
        }
    }

//    public void genDAG() {
//        for (FuncBlock funcBlock:funcBlocks) {
//            funcBlock.genDAG();
//        }
//        getOptimizedMidCode();
//    }

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        optimizedMidCode = new ArrayList<>();
        for (FuncBlock funcBlock:funcBlocks) {
            optimizedMidCode.addAll(funcBlock.getOptimizedMidCode());
        }
        return optimizedMidCode;
    }

    public void genDataFlow() {
        genDefUse();
    }

    public void genReachDef() {
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.genReachDef();
        }
    }

    public void genDefUse() {
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.genDefUse();
        }
    }

    public void dye() {
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.dye();
        }
    }

    public void delDeadCode() {
        //注意死代码删除后符号表中可能存在没有意义的项
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.delDeadCode();
        }
    }

    public void spread() {
        //基本块内的常量 、 复写传播
        for (FuncBlock funcBlock:funcBlocks) {
            funcBlock.spread();
        }
    }

    public void delWhile() {
        for (FuncBlock funcBlock:funcBlocks) {
            if (funcBlock.getFunc().equals("main")) {
                funcBlock.delWhile(funcBlocks);
            }
        }
    }
}
