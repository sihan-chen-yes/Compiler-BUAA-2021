package ASTNode;

import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
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
            String temp3 = MidCodeGener.genTemp();
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
