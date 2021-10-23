package ASTNode;
import Enum.*;
public class Exp extends Node {
    private Node AddExp;

    public Exp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExp = node;
    }

    public Node getAddExp() {
        return AddExp;
    }

    public void checkError() {
        AddExp.checkError();
    }

    public DataType getDataType() {
        assert AddExp instanceof AddExp;
        return ((AddExp) AddExp).getDataType();
    }
}
