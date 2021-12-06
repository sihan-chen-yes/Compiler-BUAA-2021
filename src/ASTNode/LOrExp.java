package ASTNode;

import java.util.ArrayList;
public class LOrExp extends Node {
    private ArrayList<LAndExp> LAndExps = new ArrayList<>();
    private boolean isWhileCond = false;

    public LOrExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LAndExps.add((LAndExp) node);
    }

    public boolean isWhileCond() {
        return isWhileCond;
    }

    public void checkError() {
        for (Node landexp:LAndExps) {
            landexp.checkError();
        }
    }

    public String getLabel2() {
        WhileStmt node = (WhileStmt) getFather().getFather();
        return node.getLabel2();
    }

    public int getLAndExpNum() {
        return LAndExps.size();
    }

    public String genMidCode(boolean again) {
        Node node = getFather().getFather();
        String notLastLabel;
        String lastLabel;
        if (node instanceof IfStmt) {
            notLastLabel = ((IfStmt) node).getLabel1();
            lastLabel = ((IfStmt) node).getLabel2();
        } else {
            assert node instanceof WhileStmt;
            isWhileCond = true;
            notLastLabel = ((WhileStmt) node).getLabel2();
            lastLabel = ((WhileStmt) node).getLabel3();
        }
        for (int i = 0;i < LAndExps.size();i++) {
            if (i == LAndExps.size() - 1) {
                LAndExps.get(i).genMidCode(true,lastLabel,again);
            } else {
                LAndExps.get(i).genMidCode(false,notLastLabel,again);
            }
        }
        return super.genMidCode();
    }
}
