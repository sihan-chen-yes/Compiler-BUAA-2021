package ASTNode;

import java.util.ArrayList;

public class RelExp extends Node {
    private ArrayList<Node> AddExps = new ArrayList<>();

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
}
