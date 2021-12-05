package ASTNode;
import Enum.DataType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    private DataType dataType = null;
    private ConstExp constExp = null;
    private ArrayList<ConstInitVal> constInitVals = new ArrayList<>();

    public ConstInitVal(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof ConstExp) {
            constExp = (ConstExp) node;
        } else if (node instanceof ConstInitVal) {
            constInitVals.add((ConstInitVal) node);
        }
    }

    public void checkError() {
        if (constExp != null) {
            constExp.checkError();
        }
        for (ConstInitVal constInitVal:constInitVals) {
            constInitVal.checkError();
        }
    }

    public int getValue() {
        //编译期间求值
        return constExp.getValue();
    }

    public ArrayList<Integer> getValues1D() {
        //编译期间求值
        ArrayList<Integer> values1D = new ArrayList<>();
        for (ConstInitVal constInitVal:constInitVals) {
            values1D.add(constInitVal.getValue());
        }
        return values1D;
    }

    public ArrayList<ArrayList<Integer>> getValues2D() {
        //编译期间求值
        ArrayList<ArrayList<Integer>> values2D = new ArrayList<>();
        for (ConstInitVal constInitVal:constInitVals) {
            values2D.add(constInitVal.getValues1D());
        }
        return values2D;
    }
}
