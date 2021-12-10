package Optimizer;

import MidCodeGeneration.MidCodeEntry;
public class HeadBlock extends BasicBlock {
    private MidCodeEntry midCodeEntry;
    private FuncBlock funcBlock;

    public HeadBlock(MidCodeEntry midCodeEntry) {
        this.midCodeEntry = midCodeEntry;
    }

    public MidCodeEntry getMidCodeEntry() {
        return midCodeEntry;
    }

    @Override
    public String toString() {
        return midCodeEntry.toString();
    }
}
