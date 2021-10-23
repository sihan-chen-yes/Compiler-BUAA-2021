package ASTNode;


public class ConstExp extends Node {
    private Node AddExp;

    public ConstExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExp = node;
    }
}
