package ASTNode;


import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class MainFuncDef extends Node {
    private Node block;

    public MainFuncDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        block = node;
    }

    public void checkError() {
        ErrorAnalysis.startFuncDef(getName());
        ArrayList<FuncFParam> FParams = new ArrayList<>();
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(), DeclType.FUNC,DataType.INT,FParams);
        if (!symbolTable.insertGlobal(symbolTableEntry)) {
            ErrorAnalysis.addError(getLine(), ErrorType.reDef);
        }
        block.checkError();
        if (block instanceof Block && !((Block) block).lastReturned()) {
            ErrorAnalysis.addError(getLine(), ErrorType.unReturn);
        }
    }
}
