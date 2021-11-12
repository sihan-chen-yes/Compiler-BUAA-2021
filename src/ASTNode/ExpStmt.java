package ASTNode;

public class ExpStmt extends Node {
    private Exp exp;

    public ExpStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        exp = (Exp) node;
    }

    public void checkError() {
        exp.checkError();
    }

    @Override
    public String genMidCode() {
        return exp.genMidCode();
    }
}
