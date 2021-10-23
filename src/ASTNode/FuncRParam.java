package ASTNode;
import Enum.DataType;

public class FuncRParam extends Node {
    private Exp exp;

    public FuncRParam(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        assert node instanceof Exp;
        exp = (Exp) node;
    }

    public DataType getDataType() {
        return exp.getDataType();
    }

    public void checkError() {
        exp.checkError();
    }
}
