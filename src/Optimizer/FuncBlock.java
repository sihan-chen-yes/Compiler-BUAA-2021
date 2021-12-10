package Optimizer;

import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;

import java.util.ArrayList;

public class FuncBlock {
    private HeadBlock headBlock;
    private ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private EndBlock endBlock = new EndBlock();
    private String func;

    public FuncBlock(MidCodeEntry midCodeEntry) {
        headBlock = new HeadBlock(midCodeEntry);
        func = midCodeEntry.getDst();
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlock.setFunc(func);
        basicBlocks.add(basicBlock);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public HeadBlock getHeadBlock() {
        return headBlock;
    }

    public EndBlock getEndBlock() {
        return endBlock;
    }

//    public void genDAG() {
//        for (BasicBlock basicBlock:basicBlocks) {
//            basicBlock.genDAG();
//        }
//    }

    public String getFunc() {
        return func;
    }

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        ArrayList<MidCodeEntry> optimizedMidCode = new ArrayList<>();
        optimizedMidCode.add(headBlock.getMidCodeEntry());
        for(BasicBlock basicBlock:basicBlocks) {
            optimizedMidCode.addAll(basicBlock.getOptimizedMidCode());
        }
        return optimizedMidCode;
    }

    public void genReachDef() {
        for (BasicBlock basicBlock:basicBlocks) {
            for (MidCodeEntry midCodeEntry:basicBlock.getMidCodeList()) {
                //更新每个midcode的kill
                if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                    midCodeEntry.genGenKill(this);
                }
            }
        }
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genGenKillSet();
        }
    }

    public void genDefUse() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genUseDefSet();
        }
        while (true) {
            boolean stop = true;
            for (int i = basicBlocks.size() - 1;i >= 0;i--) {
                if (basicBlocks.get(i).genUseDefOutSet()) {
                    stop = false;
                }
                basicBlocks.get(i).genUseDefInSet();
            }
            if (stop) {
                break;
            }
        }
    }

    public void allocSRegs() {
        basicBlocks.get(0).resetSRegs();
        for (int i = 0;i < basicBlocks.size();i++) {
            BasicBlock basicBlock = basicBlocks.get(i);
            if (i != 0) {
                basicBlock.setAllocation(basicBlock.getPreBlocks().get(0));
                //随便选一个前驱
            }
            basicBlock.allocSRegs();
            //不是第一块
        }
    }

    @Override
    public String toString() {
        String results = headBlock.toString() + "\n";
        for (int i = 0;i < basicBlocks.size();i++) {
            results += basicBlocks.get(i).toString() + "\n";
        }
        return results;
    }
}
