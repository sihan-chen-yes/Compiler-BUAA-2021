package ASTNode;

import Enum.DataType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import WordAnalysis.Word;
import Enum.*;

import java.util.ArrayList;

public class LVal extends Node {
    private ArrayList<Exp> exps = new ArrayList<>();
    private DataType dataType;
    private int length1D;
    private int length2D;

    public LVal(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        exps.add((Exp) node);
    }

    public int getBrackNum() {
        return exps.size();
    }

    public DataType getDataType() {
        return dataType;
    }

    public void checkError() {
        SymbolTable symbolTable = ErrorAnalysis.getSymbolTable();
        int layer = ErrorAnalysis.getLayer();
        if (layer == 0) {
            dataType = symbolTable.queryGlobalDataType(getName());
            if (dataType == DataType.UNDEFINED) {
                ErrorAnalysis.addError(getLine(), ErrorType.unDef);
            } else if (dataType == DataType.INT_ARRAY_1D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT;
                }
            } else if (dataType == DataType.INT_ARRAY_2D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT_ARRAY_1D;
                } else if (exps.size() == 2) {
                    dataType = DataType.INT;
                }
            }
        } else {
            dataType = symbolTable.queryLocalDataType(getName(),ErrorAnalysis.getFuncName());
            if (dataType == DataType.UNDEFINED) {
                ErrorAnalysis.addError(getLine(), ErrorType.unDef);
            } else if (dataType == DataType.INT_ARRAY_1D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT;
                }
            } else if (dataType == DataType.INT_ARRAY_2D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT_ARRAY_1D;
                } else if (exps.size() == 2) {
                    dataType = DataType.INT;
                }
            }
        }
        for (Exp exp:exps) {
            exp.checkError();
        }
    }

    public void setLength() {
        if (dataType == DataType.INT_ARRAY_1D) {
            length1D = exps.get(0).getValue();
        } else if (dataType == DataType.INT_ARRAY_2D) {
            length1D = exps.get(0).getValue();
            length2D = exps.get(1).getValue();
        }
    }

    public int getLength1D() {
        return length1D;
    }

    public int getLength2D() {
        return length2D;
    }
}
