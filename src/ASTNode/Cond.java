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

    public int getLAndExpNum() {
        return LOrExp.getLAndExpNum();
    }

    public String genMidCode() {
        LOrExp.genMidCode();
        return super.genMidCode();
    }
}
