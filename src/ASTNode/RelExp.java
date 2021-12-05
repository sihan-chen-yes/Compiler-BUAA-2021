package ASTNode;

import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelExp extends Node {
    private ArrayList<Node> AddExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

    public RelExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExps.add(node);
    }

    public void checkError() {
        for (Node addexp:AddExps) {
            addexp.checkError();
        }
    }

    public void insertCaltype(String word) {
        assert word.equals("<") || word.equals("<=") || word.equals(">") || word.equals(">=");
        if (word.equals("<")) {
            calTypes.add(CalType.lt);
        } else if (word.equals("<=")) {
            calTypes.add(CalType.le);
        } else if (word.equals(">")) {
            calTypes.add(CalType.gt);
        } else {
            calTypes.add(CalType.ge);
        }
    }

    @Override
    public String genMidCode() {
        //Todo 之后要做公共子表达式优化 T要用不同的下标
        String temp1 = AddExps.get(0).genMidCode();
        for (int i = 1;i < AddExps.size();i++) {
            String temp2 = AddExps.get(i).genMidCode();
            CalType calType = calTypes.get(i - 1);
            OpType op = null;
            if (calType == CalType.lt) {
                op = OpType.SLT;
            } else if (calType == CalType.le) {
                op = OpType.SLE;
            } else if (calType == CalType.gt) {
                op = OpType.SGT;
            } else if (calType == CalType.ge) {
                op = OpType.SGE;
            }
            //Todo
            String temp3;
            if (isNumber(temp1) && isNumber(temp2)) {
                int value1 = Integer.parseInt(temp1);
                int value2 = Integer.parseInt(temp2);
                if (op == OpType.SLT && value1 < value2 || op == OpType.SLE && value1 <= value2 ||
                    op == OpType.SGT && value1 > value2 || op == OpType.SGE && value1 >= value2) {
                    temp3 = "1";
                } else {
                    temp3 = "0";
                }
            } else {
                temp3 = MidCodeGener.genTemp();
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
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
