package ASTNode;

import Enum.DataType;
import Enum.CalType;
import java.util.ArrayList;
public class AddExp extends Node {
    private ArrayList<MulExp> MulExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

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

    public int getValue() {
        int value = MulExps.get(0).getValue();
        for (int i = 1;i < MulExps.size();i++) {
            if (calTypes.get(i - 1) == CalType.add) {
                value += MulExps.get(i).getValue();
            } else {
                assert calTypes.get(i - 1) == CalType.sub;
                value -= MulExps.get(i).getValue();
            }
        }
        return value;
    }

    public void insertCaltype(String word) {
        assert word.equals("+") || word.equals("-");
        if (word.equals("+")) {
            calTypes.add(CalType.add);
        } else {
            calTypes.add(CalType.sub);
        }
    }
}
