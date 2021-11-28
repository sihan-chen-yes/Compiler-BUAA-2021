package ASTNode;

public class ConstExp extends Node {
    private AddExp AddExp;

    public ConstExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExp = (AddExp) node;
    }

    public void checkError() {
        AddExp.checkError();
    }

    public int getValue() {
        return AddExp.getValue();
    }
}
