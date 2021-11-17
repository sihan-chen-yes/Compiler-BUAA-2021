package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
public class ContinueStmt extends Node {
    private WhileStmt whileStmt;
    public ContinueStmt(Word word, int pos) {
        super(word,pos);
    }

    public void checkError() {
        //一直向上如果始终没有WhileStmt 说明不在循环块中
        whileStmt = getWhileStmt();
        if (whileStmt == null) {
            ErrorAnalysis.addError(getLine(), ErrorType.loopError);
        }
    }

    @Override
    public String genMidCode() {
        String label1 = whileStmt.getLabel1();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.GOTO,
                        null,null,null,
                        label1
                )
        );
        return super.genMidCode();
    }

    public WhileStmt getWhileStmt() {
        Node father = this.getFather();
        while (!(father instanceof CompUnit)) {
            if (father instanceof WhileStmt) {
                return (WhileStmt) father;
            }
            father = father.getFather();
        }
        return null;
    }
}
