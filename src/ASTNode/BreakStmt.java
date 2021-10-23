package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import WordAnalysis.Word;
import Enum.*;

public class BreakStmt extends Node {
    public BreakStmt(Word word,int pos) {
        super(word,pos);
    }

    public void checkError() {
        //一直向上如果始终没有WhileStmt 说明不在循环块中
        Node father = this.getFather();
        while (!(father instanceof CompUnit)) {
            if (father instanceof WhileStmt) {
                return;
            }
            father = father.getFather();
        }
        ErrorAnalysis.addError(getLine(), ErrorType.loopError);
    }
}
