package ASTNode;

import Enum.DataType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class FuncCall extends Node {
    private FuncRParams FuncRParams ;

    public FuncCall(Word word, int pos) {
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

    @Override
    public String genMidCode() {
        MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.PREPARE_CALL,null,null,null,getName()));
        //保存上下文
        ArrayList<FuncRParam> funcRParams;
        if (FuncRParams == null) {
            funcRParams = new ArrayList<>();
        } else {
            funcRParams = FuncRParams.getFuncRParams();
        }
        int i = 0;
        for (FuncRParam funcRParam:funcRParams) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(OpType.PUSH_PARAM,funcRParam.genMidCode(),Integer.toString(i++),null,getName()));
        }
        MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.CALL,null,null,null,getName()));
        //拉栈 跳转
        MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.FIN_CALL,null,null,null,getName()));
        //还原栈 恢复上下文
        if (getDataType() == DataType.INT) {
            String temp = MidCodeGener.genTemp();
            MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.STORE_RET,null,null,null,temp));
            return temp;
        } else {
            return super.genMidCode();
        }
    }
}
