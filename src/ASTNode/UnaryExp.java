package ASTNode;

import Enum.*;
public class UnaryExp extends Node {
    private Node unary;

    public UnaryExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        unary = node;
    }

    public void checkError() {
        unary.checkError();
    }

    public DataType getDataType() {
        if (unary instanceof Exp) {
            return ((Exp) unary).getDataType();
        } else if (unary instanceof LVal) {
            return ((LVal) unary).getDataType();
        } else if (unary instanceof Number) {
            return DataType.INT;
        } else {
            assert unary instanceof FuncCall;
            return ((FuncCall) unary).getDataType();
        }
    }
}
