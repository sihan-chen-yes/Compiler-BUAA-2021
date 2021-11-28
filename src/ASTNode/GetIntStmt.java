package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class GetIntStmt extends Node {
    private LVal lval;

    public GetIntStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        lval = (LVal) node;
    }

    public LVal getLVal() {
        return lval;
    }

    public void checkError() {
        lval.checkError();
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
        assert lval.getDataType() == DataType.INT;
        if (lval.getIdentType() == DataType.INT) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(OpType.ASSIGN,
                            MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), lval.getWord()),
                            null,
                            null,
                            temp)
            );
        } else if (lval.getIdentType() == DataType.INT_ARRAY_1D) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry (
                            OpType.STORE_ARRAY_1D,
                            MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), lval.getWord()),
                            lval.getI(),
                            null,
                            temp
                    )
            );
        } else {
            assert lval.getIdentType() == DataType.INT_ARRAY_2D;
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry (
                            OpType.STORE_ARRAY_2D,
                            MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), lval.getWord()),
                            lval.getI(),
                            lval.getJ(),
                            temp
                    )
            );
        }
        return super.genMidCode();
    }
}
