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
}
