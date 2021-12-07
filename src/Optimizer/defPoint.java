package Optimizer;

public class defPoint {
    private String varName;
    private int blockNum;
    private int midCodeNum;

    public defPoint(String varName, int blockNum, int midCodeNum) {
        this.varName = varName;
        this.blockNum = blockNum;
        this.midCodeNum = midCodeNum;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public int getMidCodeNum() {
        return midCodeNum;
    }

    public void setMidCodeNum(int midCodeNum) {
        this.midCodeNum = midCodeNum;
    }
}
