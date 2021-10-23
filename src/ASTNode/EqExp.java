package ASTNode;

import java.util.ArrayList;

public class EqExp extends Node {
    private ArrayList<Node> RelExps = new ArrayList<>();

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
}
