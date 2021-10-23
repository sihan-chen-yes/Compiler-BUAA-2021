package ASTNode;

import Enum.*;
public class UnaryExp extends Node {
    private UnaryType unaryType;
    private Node unary;

    public UnaryExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        unary = node;
        if (unary instanceof FuncCall) {
            unaryType = UnaryType.FUNCCALL;
        } else if (unary instanceof Exp) {
            unaryType = UnaryType.EXP;
        } else if (unary instanceof LVal) {
            unaryType = UnaryType.LVAL;
        } else {
            unaryType = UnaryType.NUMBER;
        }
    }

    public UnaryType getUnaryType() {
        return unaryType;
    }
}
