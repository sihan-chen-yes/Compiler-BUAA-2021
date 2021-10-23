package ASTNode;

public class IfStmt extends Node {
    private Node Cond;
    private Node IfStmt = null;
    private Node ElseStmt = null;

    public IfStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = node;
        } else if (IfStmt == null) {
            IfStmt = node;
        } else {
            ElseStmt = node;
        }
    }
}
