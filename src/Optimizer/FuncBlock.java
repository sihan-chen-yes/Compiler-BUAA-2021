package Optimizer;

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

    public BasicBlock getHeadBlock() {
        return headBlock;
    }

    public void genDAG() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genDAG();
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

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        ArrayList<MidCodeEntry> optimizedMidCode = new ArrayList<>();
        optimizedMidCode.add(headBlock.getMidCodeEntry());
        for(BasicBlock basicBlock:basicBlocks) {
            optimizedMidCode.addAll(basicBlock.getOptimizedMidCode());
        }
        return optimizedMidCode;
    }
}
