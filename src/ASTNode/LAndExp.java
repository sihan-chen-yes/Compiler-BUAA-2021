package ASTNode;

import java.util.ArrayList;

public class LAndExp extends Node {
    private ArrayList<Node> EqExps =new ArrayList<>();

    public LAndExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        EqExps.add(node);
    }

    public void checkError() {
        for (Node eqexp:EqExps) {
            eqexp.checkError();
        }
    }
}
