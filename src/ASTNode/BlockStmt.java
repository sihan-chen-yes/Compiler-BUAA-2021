package ASTNode;

public class BlockStmt extends Node {
    private Node block;

    public BlockStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        block = node;
    }

    public Node getBlock() {
        return block;
    }

    public void checkError() {
        block.checkError();
    }
}
