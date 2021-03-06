package ASTNode;

import Enum.CalType;
import Enum.DataType;
import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            OpType op = null;
            if (calType == CalType.add) {
                op = OpType.ADD;
            } else if (calType == CalType.sub) {
                op = OpType.SUB;
            }
            String temp3;
            if (isNumber(temp1) && isNumber(temp2)) {
                int value1 = Integer.parseInt(temp1);
                int value2 = Integer.parseInt(temp2);
                int value3;
                if (op == OpType.ADD) {
                    value3 = value1 + value2;
                } else {
                    value3 = value1 - value2;
                }
                temp3 = Integer.toString(value3);
            } else if (isNumber(temp1) && Integer.parseInt(temp1) == 0) {
                temp3 = temp2;
            } else if (isNumber(temp2) && Integer.parseInt(temp2) == 0) {
                temp3 = temp1;
            } else {
                temp3 = MidCodeGener.genTemp();
                MidCodeGener.addMidCodeEntry(new MidCodeEntry(
                        op,
                        temp1,
                        temp2,
                        null,
                        temp3
                ));
            }
            temp1 = temp3;
        }
        return temp1;
    }

    public boolean isNumber(String name) {
        Pattern pattern = Pattern.compile("^(-)?\\d+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
