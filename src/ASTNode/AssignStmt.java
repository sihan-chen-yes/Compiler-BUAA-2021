package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class AssignStmt extends Node {
    private LVal lval;
    private Exp exp;

    public AssignStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof LVal) {
            lval = (LVal) node;
        } else {
            exp = (Exp) node;
        }
    }

    public LVal getLVal() {
        return lval;
    }

    @Override
    public void checkError() {
        lval.checkError();
        exp.checkError();
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        if (symbolTable.queryLocalDefined(getLVal().getName(),ErrorAnalysis.getFuncName())
                && symbolTable.isConst(ErrorAnalysis.getFuncName(), lval.getName())) {
            ErrorAnalysis.addError(lval.getLine(),ErrorType.constAssign);
        }
    }

    @Override
    public String genMidCode() {
        assert lval.getDataType() == DataType.INT;
        if (lval.getIdentType() == DataType.INT) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(OpType.ASSIGN,
                            MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), lval.getWord()),
                            null,
                            null,
                            exp.genMidCode())
            );
        } else if (lval.getIdentType() == DataType.INT_ARRAY_1D) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry (
                            OpType.STORE_ARRAY_1D,
                            MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(), lval.getWord()),
                            lval.getI(),
                            null,
                            exp.genMidCode()
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
                            exp.genMidCode()
                    )
            );
        }
        return super.genMidCode();
    }
}
