package ASTNode;

import Enum.DataType;
import GrammarAnalysis.ErrorAnalysis;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
public class ReturnStmt extends Node {
    private Exp Exp = null;

    public ReturnStmt(Word word, int pos) {
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

    @Override
    public String genMidCode() {
        if (MidCodeGener.getFuncName().equals("main")) {
            MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.EXIT,null,null,null,null));
        } else {
            if (Exp != null) {
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(OpType.RET_VALUE,null,null,null,Exp.genMidCode())
                );
            } else {
                MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.RET_VOID,null,null,null,null));
            }
        }
        return super.genMidCode();
    }
}
