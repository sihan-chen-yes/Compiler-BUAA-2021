package ASTNode;
import Enum.*;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import GrammarAnalysis.SymbolTableEntry;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;

import java.util.ArrayList;

public class VarDef extends Node {
    private DataType dataType;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private InitVal InitVal = null;
    private int dim;

    public VarDef(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof InitVal) {
            InitVal = (InitVal) node;
        } else if (node instanceof ConstExp) {
            constExps.add((ConstExp) node);
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
        VarDecl varDecl = (VarDecl) this.getFather();
        //全局
        if (ErrorAnalysis.getLayer() == 0) {
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),varDecl.getDeclType(),
                    dataType);
            if (!symbolTable.insertGlobal(symbolTableEntry)) {
                ErrorAnalysis.addError(getLine(),ErrorType.reDef);
            }
        } else {
            //局部
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),varDecl.getDeclType(),
                    dataType,ErrorAnalysis.getLayer());
            if (!symbolTable.insertLocal(symbolTableEntry,ErrorAnalysis.getFuncName())) {
                ErrorAnalysis.addError(getLine(),ErrorType.reDef);
            }
        }
        for (ConstExp constExp:constExps) {
            constExp.checkError();
        }
        if (InitVal != null) {
            InitVal.checkError();
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
        if (InitVal == null) {
            return 0;
        }
        return InitVal.getValue();
    }

    public ArrayList<Integer> getValues1D() {
        assert dim != 0;
        if (InitVal == null) {
            ArrayList<Integer> values1D = new ArrayList<>();
            for (int i = 0;i < getLength1D();i++) {
                values1D.add(0);
            }
            return values1D;
        }
        return InitVal.getValues1D();
    }

    public ArrayList<ArrayList<Integer>> getValues2D() {
        assert dim != 0;
        if (InitVal == null) {
            ArrayList<ArrayList<Integer>> values2D = new ArrayList<>();
            for (int i = 0;i < getLength1D();i++) {
                ArrayList<Integer> values1D = new ArrayList<>();
                for (int j = 0;j < getLength2D();j++) {
                    values1D.add(0);
                }
                values2D.add(values1D);
            }
            return values2D;
        }
        return InitVal.getValues2D();
    }

    @Override
    public String genMidCode() {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        VarDecl varDecl = (VarDecl) this.getFather();
        //全局
        if (MidCodeGener.getLayer() == 0) {
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),varDecl.getDeclType(),
                    dataType);
            setEntryInfo(symbolTableEntry);
            symbolTable.insertGlobal(symbolTableEntry);
            MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.GLOBAL_DECLARE,
                    null,null,null,getName()));
        } else {
            //局部
            SymbolTableEntry symbolTableEntry = new SymbolTableEntry(getWord(),varDecl.getDeclType(),
                    dataType,MidCodeGener.getLayer());
            setEntryDim(symbolTableEntry);
            symbolTable.insertLocal(symbolTableEntry,MidCodeGener.getFuncName());
            String Ident = getName() +  getLine();
            if (getDim() == 0) {
                String temp = InitVal.genMidCode();
                MidCodeGener.addMidCodeEntry(new MidCodeEntry(OpType.ASSIGN,Ident,null,null,temp));
            } else if (getDim() == 1) {
                ArrayList<InitVal> initVals = InitVal.getInitVals();
                for (int i = 0;i < initVals.size();i++) {
                    String temp = initVals.get(i).genMidCode();
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.STORE_ARRAY_1D,
                                    Ident,
                                    Integer.toString(i),
                                    null,
                                    temp));
                }
            } else {
                assert dim == 2;
                ArrayList<InitVal> initVals2D = InitVal.getInitVals();
                for (int i = 0;i < initVals2D.size();i++) {
                    ArrayList<InitVal> initVals1D = initVals2D.get(i).getInitVals();
                    for (int j = 0;j < initVals1D.size();j++) {
                        String temp = initVals1D.get(j).genMidCode();
                        MidCodeGener.addMidCodeEntry(
                                new MidCodeEntry(
                                        OpType.STORE_ARRAY_2D,
                                        Ident,
                                        Integer.toString(i),
                                        Integer.toString(j),
                                        temp));
                    }
                }
            }
        }
        return super.genMidCode();
    }

    public void setEntryInfo(SymbolTableEntry symbolTableEntry) {
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
        symbolTableEntry.setgpAddr();
    }

    public void setEntryDim(SymbolTableEntry symbolTableEntry) {
        if (dim == 1) {
            symbolTableEntry.setLength1D(getLength1D());
        } else if (dim == 2) {
            symbolTableEntry.setLength1D(getLength1D());
            symbolTableEntry.setLength2D(getLength2D());
        }
        symbolTableEntry.setSize();
    }
}
