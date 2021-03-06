package Optimizer;

import java.util.Objects;

public class DefPoint {
    private String varName;
    private int blockNum;
    private int midCodeNum;

    public DefPoint(String varName, int blockNum, int midCodeNum) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefPoint defPoint = (DefPoint) o;
        return getBlockNum() == defPoint.getBlockNum() && getMidCodeNum() == defPoint.getMidCodeNum() && Objects.equals(getVarName(), defPoint.getVarName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVarName(), getBlockNum(), getMidCodeNum());
    }
}
