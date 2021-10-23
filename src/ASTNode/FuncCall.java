package ASTNode;

import Enum.DataType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class FuncCall extends Node {
    private FuncRParams FuncRParams ;

    public FuncCall(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        this.FuncRParams = (FuncRParams) node;
    }

    public FuncRParams getFuncRParams() {
        return FuncRParams;
    }

    public DataType getDataType() {
        return ErrorAnalysis.getSymbolTable().queryFuncReturn(getName());
    }

    public void checkError() {
        if (FuncRParams != null) {
            FuncRParams.checkError();
        }
        int num;
        if (FuncRParams == null) {
            num = 0;
        } else {
            num = FuncRParams.getFuncRParmaNum();
        }
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        if (!symbolTable.queryFunc(getName())) {
            ErrorAnalysis.addError(getLine(),ErrorType.unDef);
        } else if (symbolTable.queryFuncParamNum(getName()) != num) {
            ErrorAnalysis.addError(getLine(), ErrorType.paramsNumError);
        } else {
            ArrayList<FuncFParam> funcFParams = symbolTable.queryFuncFParam(getName());
            ArrayList<FuncRParam> funcRParams;
            if (FuncRParams == null) {
                funcRParams = new ArrayList<>();
            } else {
                funcRParams = FuncRParams.getFuncRParams();
            }
            for (int i = 0;i < funcRParams.size();i++) {
                if (funcRParams.get(i).getDataType() == DataType.UNDEFINED) {
                    return;
                } else if (funcRParams.get(i).getDataType() != funcFParams.get(i).getDataType()) {
                    ErrorAnalysis.addError(getLine(),ErrorType.paramsTypeError);
                }
            }
        }
    }
}
