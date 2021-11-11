package ASTNode;

import java.util.ArrayList;
public class LOrExp extends Node {
    private ArrayList<Node> LAndExps = new ArrayList<>();

    public LOrExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LAndExps.add(node);
    }

    public void checkError() {
        for (Node landexp:LAndExps) {
            landexp.checkError();
        }
    }
}
