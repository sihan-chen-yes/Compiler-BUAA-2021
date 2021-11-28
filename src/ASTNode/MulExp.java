package ASTNode;

import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;

public class MulExp extends Node {
    private ArrayList<UnaryExp> UnaryExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

    public MulExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        UnaryExps.add((UnaryExp) node);
    }

    public void checkError() {
        for (Node node:UnaryExps) {
            node.checkError();
        }
    }

    public DataType getDataType() {
        if (UnaryExps.size() == 1) {
            assert UnaryExps.get(0) instanceof UnaryExp;
            return UnaryExps.get(0).getDataType();
        } else {
            return DataType.INT;
        }
    }

    public int getValue() {
        int value = UnaryExps.get(0).getValue();
        for (int i = 1;i < UnaryExps.size();i++) {
            if (calTypes.get(i - 1) == CalType.mul) {
                value *= UnaryExps.get(i).getValue();
            } else if (calTypes.get(i - 1) == CalType.div) {
                value /= UnaryExps.get(i).getValue();
            } else {
                assert calTypes.get(i - 1) == CalType.mod;
                value %= UnaryExps.get(i).getValue();
            }
        }
        return value;
    }

    public void insertCaltype(String word) {
        assert word.equals("*") || word.equals("/") || word.equals("%");
        if (word.equals("*")) {
            calTypes.add(CalType.mul);
        } else if (word.equals("/")) {
            calTypes.add(CalType.div);
        } else {
            calTypes.add(CalType.mod);
        }
    }

    @Override
    public String genMidCode() {
        String temp1 = UnaryExps.get(0).genMidCode();
        for (int i = 1;i < UnaryExps.size();i++) {
            String temp2 = UnaryExps.get(i).genMidCode();
            CalType calType = calTypes.get(i - 1);
            String temp3 = MidCodeGener.genTemp();
            OpType op = null;
            if (calType == CalType.mul) {
                op = OpType.MULT;
            } else if (calType == CalType.div) {
                op = OpType.DIV;
            } else {
                op = OpType.MOD;
            }
            MidCodeGener.addMidCodeEntry(new MidCodeEntry(
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
