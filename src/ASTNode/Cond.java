package ASTNode;

public class Cond extends Node {
    private Node LOrExp;

    public Cond(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LOrExp = node;
    }
}