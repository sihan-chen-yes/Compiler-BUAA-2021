package ASTNode;

import Enum.CalType;
import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EqExp extends Node {
    private ArrayList<Node> RelExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

    public EqExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        RelExps.add(node);
    }

    public void checkError() {
        for (Node node:RelExps) {
            node.checkError();
        }
    }

    public void insertCaltype(String word) {
        assert word.equals("==") || word.equals("!=");
        if (word.equals("==")) {
            calTypes.add(CalType.eq);
        } else {
            calTypes.add(CalType.neq);
        }
    }

    @Override
    public String genMidCode() {
        String temp1 = RelExps.get(0).genMidCode();
        for (int i = 1;i < RelExps.size();i++) {
            String temp2 = RelExps.get(i).genMidCode();
            CalType calType = calTypes.get(i - 1);
            OpType op = null;
            if (calType == CalType.eq) {
                op = OpType.SEQ;
            } else if (calType == CalType.neq) {
                op = OpType.SNE;
            }
            String temp3;
            if (isNumber(temp1) && isNumber(temp2)) {
                int value1 = Integer.parseInt(temp1);
                int value2 = Integer.parseInt(temp2);
                if (op == OpType.SEQ && value1 == value2 || op == OpType.SNE && value1 != value2) {
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
