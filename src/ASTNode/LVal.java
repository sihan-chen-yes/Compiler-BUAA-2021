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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LVal extends Node {
    private ArrayList<Exp> exps = new ArrayList<>();
    private DataType identType;
    //Ident的类型
    private DataType dataType;
    //取下标后的类型

    //编译时 下标
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
        //checkError之后 Ident的dataType即可确定
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
        SymbolTableEntry symbolTableEntry = symbolTable.searchDefinedEntry(MidCodeGener.getFuncName(),getName());
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
        //每次都需要看一下 当前这个Ident + []表示什么dataType 注意LVal的dataType check的时候没有set
        SymbolTableEntry symbolTableEntry = MidCodeGener.getSymbolTable().
                getConstant(MidCodeGener.getFuncName(),getName());
        if (getFather() instanceof UnaryExp) {
            String Ident = MidCodeGener.getSymbolTable().getRefactorName(MidCodeGener.getFuncName(),getWord());
            //全局变量或者局部变量 refactor能够确定
            if (exps.isEmpty()) {
                if (dataType == DataType.INT) {
                    if (symbolTableEntry != null) {
                        int value = symbolTableEntry.getValue();
                        return Integer.toString(value);
                    }
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
                    String temp;
                    String i = getI();
                    if (symbolTableEntry != null && isNumber(i)) {
                        int value = symbolTableEntry.getValue1D(Integer.parseInt(i));
                        temp = Integer.toString(value);
                    } else {
                        temp = MidCodeGener.genTemp();
                        MidCodeGener.addMidCodeEntry(
                                new MidCodeEntry(
                                        OpType.LOAD_ARRAY_1D,
                                        Ident,
                                        i,
                                        null,
                                        temp));
                    }
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
                String temp;
                String i = getI();
                String j = getJ();
                if (symbolTableEntry != null && isNumber(i) && isNumber(j)) {
                    int val = symbolTableEntry.getValue2D(Integer.parseInt(i),Integer.parseInt(j));
                    temp = Integer.toString(val);
                } else {
                    temp = MidCodeGener.genTemp();
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.LOAD_ARRAY_2D,
                                    Ident,
                                    i,
                                    j,
                                    temp));
                }
                return temp;
            }
        }
        return super.genMidCode();
    }

    public boolean isNumber(String name) {
        Pattern pattern = Pattern.compile("^(-)?\\d+");
        Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
