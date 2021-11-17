package ASTNode;

import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
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
        //Todo 之后要做公共子表达式优化 T要用不同的下标
        String temp1 = RelExps.get(0).genMidCode();
        for (int i = 1;i < RelExps.size();i++) {
            String temp2 = RelExps.get(i).genMidCode();
            CalType calType = calTypes.get(i - 1);
            String temp3 = MidCodeGener.genTemp();
            OpType op = null;
            if (calType == CalType.eq) {
                op = OpType.SEQ;
            } else if (calType == CalType.neq) {
                op = OpType.SNE;
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
