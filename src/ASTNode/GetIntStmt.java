package ASTNode;

public class GetIntStmt extends Node {
    private Node LVal;

    public GetIntStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LVal = node;
    }

    public Node getLVal() {
        return LVal;
    }

    public void checkError() {
        LVal.checkError();
    }
}
