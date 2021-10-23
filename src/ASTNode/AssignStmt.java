package ASTNode;

import WordAnalysis.Word;

public class AssignStmt extends Node {
    private Node LVal;
    private Node Exp;

    public AssignStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof LVal) {
            LVal = node;
        } else {
            Exp = node;
        }
    }

    public Node getLVal() {
        return LVal;
    }

    public Node getExp() {
        return Exp;
    }
}
