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

    public void checkError() {
        block.checkError();
    }

    @Override
    public String genMidCode() {
        block.genMidCode();
        return super.genMidCode();
    }
}
