package ASTNode;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class FuncDef extends Node {
    private FuncType funcType;
    private FuncFParams funcFParams = null;
    private Block block;

    public FuncDef(Word word, int pos) {
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

    @Override
    public String genMidCode() {
        MidCodeGener.startFuncDef(getName());
        ArrayList<FuncFParam> FParams;
        if (funcFParams == null) {
            FParams = new ArrayList<>();
        } else {
            FParams = funcFParams.getFuncFParams();
        }
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),DeclType.FUNC,getDataType(),FParams);
        symbolTable.insertGlobal(symbolTableEntry);
        for (FuncFParam funcFParam:FParams) {
            symbolTableEntry = new SymbolTableEntry(funcFParam.getWord(),
                    DeclType.PARAM,funcFParam.getDataType(),MidCodeGener.getLayer());
            symbolTableEntry.setSize();
            if (funcFParam.getDataType() == DataType.INT_ARRAY_2D) {
                symbolTableEntry.setParamLength2D(funcFParam.getLength2D());
            }
            symbolTable.insertLocal(symbolTableEntry,MidCodeGener.getFuncName());
        }
        MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.FUNC_DECLARE,null,null,null,getName()));
        block.genMidCode();
        MidCodeGener.getSymbolTable().setLocalAddr(MidCodeGener.getFuncName());
        MidCodeGener.getSymbolTable().refactorName(MidCodeGener.getFuncName());
        return super.genMidCode();
    }
}
