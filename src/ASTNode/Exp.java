package ASTNode;

public class Exp extends Node {
    private Node AddExp;

    public Exp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExp = node;
    }

    public Node getAddExp() {
        return AddExp;
    }
}
