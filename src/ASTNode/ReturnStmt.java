package ASTNode;

import Enum.DataType;
import GrammarAnalysis.ErrorAnalysis;
import WordAnalysis.Word;
import Enum.*;
public class ReturnStmt extends Node {
    private Exp Exp = null;

    public ReturnStmt(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Exp = (Exp) node;
    }

    public boolean isVoid() {
        return Exp == null;
    }

    public void checkError() {
        if (Exp != null && ErrorAnalysis.getRetType() == DataType.VOID) {
            ErrorAnalysis.addError(getLine(), ErrorType.redundantReturn);
            Exp.checkError();
        }
    }
}
