package ASTNode;

import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            OpType op;
            if (calType == CalType.mul) {
                op = OpType.MULT;
            } else if (calType == CalType.div) {
                op = OpType.DIV;
            } else {
                op = OpType.MOD;
            }
            String temp3;
            if (isNumber(temp1) && isNumber(temp2)) {
                int value1 = Integer.parseInt(temp1);
                int value2 = Integer.parseInt(temp2);
                int value3;
                if (op == OpType.MULT) {
                    value3 = value1 * value2;
                } else if (op == OpType.DIV) {
                    value3 = value1 / value2;
                } else {
                    value3 = value1 % value2;
                }
                temp3 = Integer.toString(value3);
            } else if (op != OpType.MOD && isNumber(temp1) && (Integer.parseInt(temp1) == 1 || Integer.parseInt(temp1) == -1)) {
                //只有成* /
                int value1 = Integer.parseInt(temp1);
                if (value1 == 1) {
                    temp3 = temp2;
                } else {
                    assert value1 == -1;
                    temp3 = MidCodeGener.genTemp();
                    MidCodeGener.addMidCodeEntry(new MidCodeEntry(
                            OpType.NEG,
                            temp2,
                            null,
                            null,
                            temp3
                    ));
                }
            } else if (isNumber(temp2) && (Integer.parseInt(temp2) == 1 || Integer.parseInt(temp2) == -1)) {
                int value2 = Integer.parseInt(temp2);
                if (value2 == 1) {
                    if (op == OpType.MOD) {
                        temp3 = "0";
                    } else {
                        temp3 = temp1;
                    }
                } else {
                    assert value2 == -1;
                    if (op == OpType.MOD) {
                        temp3 = "0";
                    } else {
                        temp3 = MidCodeGener.genTemp();
                        MidCodeGener.addMidCodeEntry(new MidCodeEntry(
                                OpType.NEG,
                                temp1,
                                null,
                                null,
                                temp3
                        ));
                    }
                }
            } else if (op == OpType.MULT && (isNumber(temp1) && Integer.parseInt(temp1) == 0 ||
                    isNumber(temp2) && Integer.parseInt(temp2) == 0)) {
                temp3 = "0";
            } else if ((op == OpType.MOD || op == OpType.DIV) && isNumber(temp1) && Integer.parseInt(temp1) == 0) {
                temp3 = "0";
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
