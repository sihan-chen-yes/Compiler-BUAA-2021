package ASTNode;

import java.util.ArrayList;

public class Block extends Node {
    private ArrayList<Node> Stmts = new ArrayList<>();

    public Block(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Stmts.add(node);
    }

    public ArrayList<Node> getStmts() {
        return Stmts;
    }
}
