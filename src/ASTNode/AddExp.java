package ASTNode;

import java.util.ArrayList;

public class AddExp extends Node {
    private ArrayList<Node> MulExps = new ArrayList<>();

    public AddExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        MulExps.add(node);
    }

    public ArrayList<Node> getMulExps() {
        return MulExps;
    }
}
