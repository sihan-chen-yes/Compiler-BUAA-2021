package ASTNode;
import Enum.*;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import WordAnalysis.Word;

import java.util.ArrayList;

public class FuncDef extends Node {
    private FuncType funcType;
    private FuncFParams funcFParams = null;
    private Block block;

    public FuncDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof FuncType) {
            funcType = (FuncType) node;
        } else if (node instanceof FuncFParams){
            funcFParams = (FuncFParams) node;
        } else {
            block = (Block) node;
        }
    }

    public DataType getDataType() {
        return funcType.getDataType();
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        if (funcFParams == null) {
            return new ArrayList<>();
        }
        return funcFParams.getFuncFParams();
    }

    public Node getBlock() {
        return block;
    }

    public DataType getFuncType() {
        return funcType.getDataType();
    }

    public void checkError() {
        ErrorAnalysis.startFuncDef(getName(),getDataType());
        if (funcFParams != null) {
            funcFParams.checkError();
        }
        ArrayList<FuncFParam> FParams;
        if (funcFParams == null) {
            FParams = new ArrayList<>();
        } else {
            FParams = funcFParams.getFuncFParams();
        }
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),DeclType.FUNC,getDataType(),FParams);
        if (!symbolTable.insertGlobal(symbolTableEntry)) {
            ErrorAnalysis.addError(getLine(),ErrorType.reDef);
        }
        block.checkError();
        if (getFuncType() == DataType.INT) {
            if (!block.lastReturned()) {
                ErrorAnalysis.addError(block.getLastRBRACE().getLine(),ErrorType.unReturn);
            }
        }
    }
}
