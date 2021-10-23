package ASTNode;
import Enum.DataType;
import Enum.ErrorType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import WordAnalysis.Word;

import java.util.ArrayList;

public class ConstDef extends Node {
    private DataType dataType;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ConstInitVal ConstInitVal = null;
    private int dim;

    public ConstDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof ConstExp) {
            constExps.add((ConstExp) node);
        } else {
            ConstInitVal = (ConstInitVal) node;
        }
    }

    public void setDimType(int dim) {
        this.dim = dim;
    }

    public void setDataType(DataType type) {
        if (dataType == DataType.INT) {
            if (dim == 0) {
                dataType = DataType.INT;
            } else if (dim == 1) {
                dataType = DataType.INT_ARRAY_1D;
            } else {
                dataType = DataType.INT_ARRAY_2D;
            }
        }
    }

    public void checkError() {
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        ConstDecl constDecl = (ConstDecl) this.getFather();
        //全局
        if (ErrorAnalysis.getLayer() == 0) {
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),constDecl.getDeclType(),
                    dataType);
            if (!symbolTable.insertGlobal(symbolTableEntry)) {
                ErrorAnalysis.addError(getLine(),ErrorType.reDef);
            }
        } else {
            //局部
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),constDecl.getDeclType(),
                    dataType,ErrorAnalysis.getLayer());
            if (!symbolTable.insertLocal(symbolTableEntry,ErrorAnalysis.getFuncName())) {
                ErrorAnalysis.addError(getLine(),ErrorType.reDef);
            }
        }
        for (ConstExp constExp:constExps) {
            constExp.checkError();
        }
        if (ConstInitVal != null) {
            ConstInitVal.checkError();
        }
    }
}
