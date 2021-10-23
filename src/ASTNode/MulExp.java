package ASTNode;

import Enum.*;
import java.util.ArrayList;

public class MulExp extends Node {
    private ArrayList<Node> UnaryExps = new ArrayList<>();

    public MulExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        UnaryExps.add(node);
    }

    public ArrayList<Node> getUnaryExps() {
        return UnaryExps;
    }

    public void checkError() {
        for (Node node:UnaryExps) {
            node.checkError();
        }
    }

    public DataType getDataType() {
        if (UnaryExps.size() == 1) {
            assert UnaryExps.get(0) instanceof UnaryExp;
            return ((UnaryExp) UnaryExps.get(0)).getDataType();
        } else {
            return DataType.INT;
        }
    }
}
