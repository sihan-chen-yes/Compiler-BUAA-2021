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

    public void genDAG() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genDAG();
        }
    }

    public String getFunc() {
        return func;
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
            ArrayList<String> labels = new ArrayList<>();
            for (String label:labels) {
                optimizedMidCode.add(new MidCodeEntry(OpType.LABEL_GEN,null,null,null,label));
            }
            optimizedMidCode.addAll(basicBlock.getOptimizedMidCode());
        }
        return optimizedMidCode;
    }
}
