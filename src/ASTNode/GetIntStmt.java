package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class GetIntStmt extends Node {
    private Node LVal;

    public GetIntStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        LVal = node;
    }

    public Node getLVal() {
        return LVal;
    }

    public void checkError() {
        LVal.checkError();
    }

    @Override
    public String genMidCode() {
        String temp = MidCodeGener.genTemp();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.GETINT,
                        null,
                        null,
                        null,
                        temp)
        );
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.ASSIGN,
                        MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), getLVal().getWord()),
                        null,null,
                        temp
                )
        );
        return super.genMidCode();
    }
}
