package ASTNode;

import Enum.CalType;
import Enum.DataType;
import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

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

    @Override
    public String genMidCode() {
        String temp1 = MulExps.get(0).genMidCode();
        for (int i = 1;i < MulExps.size();i++) {
            String temp2 = MulExps.get(i).genMidCode();
            CalType calType = calTypes.get(i - 1);
            String temp3 = MidCodeGener.genTemp();
            OpType op = null;
            if (calType == CalType.add) {
                op = OpType.ADD;
            } else if (calType == CalType.sub) {
                op = OpType.SUB;
            }
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                    op,
                    temp1,
                    temp2,
                    null,
                    temp3
                    ));
            temp1 = temp3;
        }
        return temp1;
    }
}
