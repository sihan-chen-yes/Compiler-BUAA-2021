package ASTNode;

public class Cond extends Node {
    private LOrExp LOrExp;

    public Cond(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LOrExp = (LOrExp) node;
    }

    public void checkError() {
        LOrExp.checkError();
    }
}
