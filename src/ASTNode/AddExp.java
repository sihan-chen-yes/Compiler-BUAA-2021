package ASTNode;

import Enum.DataType;

import java.util.ArrayList;
public class AddExp extends Node {
    private ArrayList<MulExp> MulExps = new ArrayList<>();

    public AddExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        MulExps.add((MulExp) node);
    }

    public ArrayList<MulExp> getMulExps() {
        return MulExps;
    }

    public void checkError() {
        for (Node mulExp:MulExps) {
            mulExp.checkError();
        }
    }

    public DataType getDataType() {
        if (MulExps.size() == 1) {
            return MulExps.get(0).getDataType();
        } else {
            return DataType.INT;
        }
    }
}
