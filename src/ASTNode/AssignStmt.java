package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import Enum.*;
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

    public Exp getExp() {
        return exp;
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
}
