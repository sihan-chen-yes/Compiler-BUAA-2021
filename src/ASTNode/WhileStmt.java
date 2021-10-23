package ASTNode;

public class WhileStmt extends Node {
    private Node Cond;
    private Node Stmt;

    public WhileStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = node;
        } else {
            Stmt = node;
        }
    }
}
