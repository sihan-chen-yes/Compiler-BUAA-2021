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

public class LVal extends Node {
    private ArrayList<Exp> exps = new ArrayList<>();
    private DataType identType;
    private DataType dataType;
    //编译时 下标
    private int length1D;
    private int length2D;

    //运行时 下标
    private int i;
    private int j;



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
            identType = symbolTable.queryGlobalDataType(getName());
            if (identType == DataType.UNDEFINED) {
                ErrorAnalysis.addError(getLine(), ErrorType.unDef);
            } else if (identType == DataType.INT_ARRAY_1D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT;
                }
            } else if (identType == DataType.INT_ARRAY_2D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT_ARRAY_1D;
                } else if (exps.size() == 2) {
                    dataType = DataType.INT;
                }
            }
        } else {
            identType = symbolTable.queryLocalDataType(getName(),ErrorAnalysis.getFuncName());
            if (identType == DataType.UNDEFINED) {
                ErrorAnalysis.addError(getLine(), ErrorType.unDef);
            } else if (identType == DataType.INT) {
                dataType = DataType.INT;
            } else if (identType == DataType.INT_ARRAY_1D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT;
                } else {
                    dataType = DataType.INT_ARRAY_1D;
                }
            } else if (identType == DataType.INT_ARRAY_2D) {
                if (exps.size() == 1) {
                    dataType = DataType.INT_ARRAY_1D;
                } else if (exps.size() == 2) {
                    dataType = DataType.INT;
                } else {
                    dataType = DataType.INT_ARRAY_2D;
                }
            }
        }
        for (Exp exp:exps) {
            exp.checkError();
        }
    }

    public void setIndex() {
        if (identType == DataType.INT_ARRAY_1D) {
            length1D = exps.get(0).getValue();
        } else if (identType == DataType.INT_ARRAY_2D) {
            length1D = exps.get(0).getValue();
            length2D = exps.get(1).getValue();
        }
    }

    public DataType getIdentType() {
        return identType;
    }

    public int getLength1D() {
        length1D = exps.get(0).getValue();
        return length1D;
    }

    public int getLength2D() {
        length2D = exps.get(1).getValue();
        return length2D;
    }

    public String getI() {
        return exps.get(0).genMidCode();
    }

    public String getJ() {
        return exps.get(1).genMidCode();
    }

    public void setDataType() {
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        SymbolTableEntry symbolTableEntry = symbolTable.searchDefinedEntry(MidCodeGener.getFuncName(),getWord());
        identType = symbolTableEntry.getDataType();
        if (identType == DataType.INT) {
            dataType = DataType.INT;
        } else if (identType == DataType.INT_ARRAY_1D) {
            if (exps.size() == 1) {
                dataType = DataType.INT;
            } else {
                dataType = DataType.INT_ARRAY_1D;
            }
        } else if (identType == DataType.INT_ARRAY_2D) {
            if (exps.size() == 1) {
                dataType = DataType.INT_ARRAY_1D;
            } else if (exps.size() == 2) {
                dataType = DataType.INT;
            } else {
                dataType = DataType.INT_ARRAY_2D;
            }
        }
    }


    @Override
    public String genMidCode() {
        setDataType();
        if (getFather() instanceof UnaryExp) {
            //只有在右边时产生MidCode
            String Ident = MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(),getWord());
            //全局变量或者局部变量 refactor能够确定
            refactor(Ident);
            if (exps.isEmpty()) {
                if (dataType == DataType.INT) {
                    return Ident;
                } else {
                    String temp = MidCodeGener.genTemp();//是一个地址
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.LOAD_ARRDESS,
                                    Ident,
                                    null,
                                    null,
                                    temp
                            )
                    );
                    return temp;
                }
            } else if (exps.size() == 1) {
                if (dataType == DataType.INT) {
                    String temp = MidCodeGener.genTemp();
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.LOAD_ARRAY_1D,
                                    Ident,
                                    getI(),
                                    null,
                                    temp));
                    return temp;
                } else {
                    assert dataType == DataType.INT_ARRAY_1D && identType == DataType.INT_ARRAY_2D;
                    String temp = MidCodeGener.genTemp();//是一个地址
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.LOAD_ARRDESS,
                                    Ident,
                                    getI(),
                                    null,
                                    temp
                            )
                    );
                    return temp;
                }
            } else {
                assert exps.size() == 2 && dataType == DataType.INT;
                String temp = MidCodeGener.genTemp();
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
                                OpType.LOAD_ARRAY_2D,
                                Ident,
                                getI(),
                                getJ(),
                                temp));
                return temp;
            }
        }
        return super.genMidCode();
    }
}
