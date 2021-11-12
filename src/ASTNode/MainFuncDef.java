package ASTNode;


import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class MainFuncDef extends Node {
    private Block block;

    public MainFuncDef(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        block = (Block) node;
    }

    public void checkError() {
        ErrorAnalysis.startFuncDef(getName(),DataType.INT);
        ArrayList<FuncFParam> FParams = new ArrayList<>();
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(), DeclType.FUNC,DataType.INT,FParams);
        if (!symbolTable.insertGlobal(symbolTableEntry)) {
            ErrorAnalysis.addError(getLine(), ErrorType.reDef);
        }
        block.checkError();
        if (!(block.lastReturned())) {
            ErrorAnalysis.addError(block.getLastRBRACE().getLine(),ErrorType.unReturn);
        }
    }

    @Override
    public String genMidCode() {
        MidCodeGener.startFuncDef(getName());
        ArrayList<FuncFParam> FParams = new ArrayList<>();
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),DeclType.FUNC,DataType.INT,FParams);
        symbolTable.insertGlobal(symbolTableEntry);
        MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.FUNC_DECLARE,null,null,null,getName()));
        block.genMidCode();
        MidCodeGener.getSymbolTable().setLocalAddr(MidCodeGener.getFuncName());
        MidCodeGener.getSymbolTable().refactorName(MidCodeGener.getFuncName());
        return super.genMidCode();
    }
}
