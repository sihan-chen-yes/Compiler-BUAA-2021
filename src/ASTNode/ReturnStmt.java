package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import WordAnalysis.Word;

public class ReturnStmt extends Node {
    private Node Exp = null;

    public ReturnStmt(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Exp = node;
    }

    public boolean isVoid() {
        return Exp == null;
    }

    public void checkError() {
        if (Exp != null) {
            ErrorAnalysis.setReturned(true);
            ErrorAnalysis.setReturned(getWord());
            Exp.checkError();
        }
    }
}
