package ASTNode;
import Enum.DataType;
import Enum.ErrorType;
import Enum.OpType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;

import java.util.ArrayList;

public class ConstDef extends Node {
    private DataType dataType;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private ConstInitVal ConstInitVal = null;
    private int dim;

    public ConstDef(Word word, int pos) {
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
        if (type == DataType.INT) {
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

    public int getDim() {
        return dim;
    }

    public int getLength1D() {
        assert constExps.size() == 1;
        return constExps.get(0).getValue();
    }

    public int getLength2D() {
        assert constExps.size() == 2;
        return constExps.get(1).getValue();
    }

    public int getValue() {
        assert dim == 0;
        return ConstInitVal.getValue();
    }

    public ArrayList<Integer> getValues1D() {
        assert dim != 0;
        return ConstInitVal.getValues1D();
    }

    public ArrayList<ArrayList<Integer>> getValues2D() {
        assert dim != 0;
        return ConstInitVal.getValues2D();
    }

    public String genMidCode() {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        ConstDecl constDecl = (ConstDecl) this.getFather();
        if (MidCodeGener.getLayer() == 0) {
            //全局
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),constDecl.getDeclType(),
                    dataType);
            setEntryInfo(symbolTableEntry,true);
            symbolTable.insertGlobal(symbolTableEntry);
            MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.GLOBAL_DECLARE,null,null,null,getName()));
        } else {
            //局部
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),constDecl.getDeclType(),
                    dataType,MidCodeGener.getLayer());
            setEntryInfo(symbolTableEntry,false);
            symbolTable.insertLocal(symbolTableEntry,MidCodeGener.getFuncName());
            //Todo 可以优化 const
            String Ident = getName() + getLine();
            if (dataType == DataType.INT) {
                int value = symbolTableEntry.getValue();
                MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.ASSIGN,Ident,null,null,Integer.toString(value)));
            } else if (dataType == DataType.INT_ARRAY_1D) {
                ArrayList<Integer> values1D = symbolTableEntry.getValues1D();
                for (int i = 0;i < values1D.size();i++) {
                    int value = values1D.get(i);
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.STORE_ARRAY_1D,
                                    Ident,
                                    Integer.toString(i),
                                    null,
                                    Integer.toString(value)));
                }
            } else {
                ArrayList<ArrayList<Integer>> values2D = symbolTableEntry.getValues2D();
                for (int i = 0;i < values2D.size();i++) {
                    ArrayList<Integer> values1D = values2D.get(i);
                    for (int j = 0;j < values1D.size();j++) {
                        int value = values1D.get(j);
                        MidCodeGener.addMidCodeEntry(
                                new MidCodeEntry(
                                        OpType.STORE_ARRAY_2D,
                                        Ident,
                                        Integer.toString(i),
                                        Integer.toString(j),
                                        Integer.toString(value)
                                        ));
                    }
                }
            }
        }
        return super.genMidCode();
    }

    public void setEntryInfo(SymbolTableEntry symbolTableEntry,boolean isGlobal) {
        if (dim == 0) {
            symbolTableEntry.setValue(getValue());
        } else if (dim == 1) {
            symbolTableEntry.setLength1D(getLength1D());
            symbolTableEntry.setValues1D(getValues1D());
        } else {
            assert dim == 2;
            symbolTableEntry.setLength1D(getLength1D());
            symbolTableEntry.setLength2D(getLength2D());
            symbolTableEntry.setValues2D(getValues2D());
        }
        symbolTableEntry.setSize();
        if (isGlobal) {
            symbolTableEntry.setgpAddr();
        }
    }
}
