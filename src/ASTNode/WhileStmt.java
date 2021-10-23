package ASTNode;

public class WhileStmt extends Node {
    private Node Cond;
    private Node body;

    public WhileStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = node;
        } else {
            body = node;
        }
    }

    public void checkError() {
        if (Cond != null) {
            Cond.checkError();
        }
        if (body != null) {
            body.checkError();
        }
    }
}
