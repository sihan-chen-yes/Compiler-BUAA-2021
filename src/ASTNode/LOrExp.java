package ASTNode;

import java.util.ArrayList;

public class LOrExp extends Node {
    private ArrayList<Node> LAndExps;

    public LOrExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LAndExps.add(node);
    }
}
