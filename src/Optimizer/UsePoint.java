package Optimizer;

import java.util.Objects;

public class UsePoint {
    private String varName;
    private int blockNum;
    private int midCodeNum;

    public UsePoint(String varName, int blockNum, int midCodeNum) {
        this.varName = varName;
        this.blockNum = blockNum;
        this.midCodeNum = midCodeNum;
    }

    public String getVarName() {
        return varName;
    }

    public int getBlockNum() {
        return blockNum;
    }

    public int getMidCodeNum() {
        return midCodeNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsePoint usePoint = (UsePoint) o;
        return getBlockNum() == usePoint.getBlockNum() && getMidCodeNum() == usePoint.getMidCodeNum() && Objects.equals(getVarName(), usePoint.getVarName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVarName(), getBlockNum(), getMidCodeNum());
    }
}
