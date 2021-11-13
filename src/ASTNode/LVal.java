package ASTNode;

import Enum.DataType;
import Enum.ErrorType;
import Enum.OpType;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.SymbolTable;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;

import java.util.ArrayList;

public class LVal extends Node {
    private ArrayList<Exp> exps = new ArrayList<>();
    private DataType identType;
    private DataType dataType;
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

    public void setLength() {
        if (identType == DataType.INT_ARRAY_1D) {
            i = exps.get(0).getValue();
        } else if (identType == DataType.INT_ARRAY_2D) {
            i = exps.get(0).getValue();
            j = exps.get(1).getValue();
        }
    }

    public DataType getIdentType() {
        return identType;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public String genMidCode() {
        if (getFather() instanceof UnaryExp) {
            //只有在右边时产生MidCode
            String Ident = MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(),getWord());
            //全局变量或者局部变量 refactor能够确定
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
                                    Integer.toString(i),
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
                                    Integer.toString(i),
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
                                Integer.toString(i),
                                Integer.toString(j),
                                temp));
                return temp;
            }
        }
        return super.genMidCode();
    }
}
