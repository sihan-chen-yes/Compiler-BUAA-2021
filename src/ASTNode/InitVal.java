package ASTNode;
import Enum.*;
import java.util.ArrayList;

public class InitVal extends Node {
    private DataType dataType;
    private Exp exp = null;
    private ArrayList<InitVal> initVals = new ArrayList<>();

    public InitVal(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Exp) {
            exp = (Exp) node;
        } else {
            initVals.add((InitVal) node);
        }
    }

    public void checkError() {
        if (exp != null) {
            exp.checkError();
        }
        for (InitVal initVal:initVals) {
            initVal.checkError();
        }
    }

    public int getValue() {
        //编译期间求值
        return exp.getValue();
    }

    public ArrayList<Integer> getValues1D() {
        //编译期间求值
        ArrayList<Integer> values1D = new ArrayList<>();
        for (InitVal initVal:initVals) {
            values1D.add(initVal.getValue());
        }
        return values1D;
    }

    public ArrayList<ArrayList<Integer>> getValues2D() {
        //编译期间求值
        ArrayList<ArrayList<Integer>> values2D = new ArrayList<>();
        for (InitVal initVal:initVals) {
            values2D.add(initVal.getValues1D());
        }
        return values2D;
    }

    public int genMidCode() {
        return exp.genMidCode();
    }

    public ArrayList<InitVal> getInitVals() {
        return initVals;
    }

}
