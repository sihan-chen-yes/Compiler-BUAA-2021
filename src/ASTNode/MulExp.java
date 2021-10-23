package ASTNode;

import java.util.ArrayList;

public class MulExp extends Node {
    private ArrayList<Node> UnaryExps = new ArrayList<>();

    public MulExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        UnaryExps.add(node);
    }

    public ArrayList<Node> getUnaryExps() {
        return UnaryExps;
    }
}
