package ASTNode;

import Optimizer.Optimizer;

import java.util.ArrayList;
public class LOrExp extends Node {
    private ArrayList<LAndExp> LAndExps = new ArrayList<>();
    private boolean isWhileCond = false;
    private boolean needOp = false;

    public LOrExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LAndExps.add((LAndExp) node);
    }

    public void checkError() {
        for (Node landexp:LAndExps) {
            landexp.checkError();
        }
    }

    public int getLAndExpNum() {
        return LAndExps.size();
    }

    public String genMidCode() {
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
        if (!needOp) {
            for (int i = 0;i < LAndExps.size();i++) {
                if (i == LAndExps.size() - 1) {
                    LAndExps.get(i).genMidCode(true,lastLabel,null);
                } else {
                    LAndExps.get(i).genMidCode(false,notLastLabel,null);
                }
            }
        } else {
            for (int i = 0;i < LAndExps.size();i++) {
                if (i == LAndExps.size() - 1) {
                    LAndExps.get(i).genMidCode(true,lastLabel,notLastLabel);
                } else {
                    LAndExps.get(i).genMidCode(false,notLastLabel,null);
                }
            }
        }
        if (isWhileCond && Optimizer.isOp()) {
            needOp = true;
        }
        return super.genMidCode();
    }
}
