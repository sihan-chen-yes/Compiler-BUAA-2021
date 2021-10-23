package ASTNode;
import Enum.DataType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    private DataType dataType;
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
}
