package ASTNode;

import Enum.CalType;
import Enum.DataType;
import Enum.*;
import GrammarAnalysis.SymbolTableEntry;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnaryExp extends Node {
    private Node unary;
    private ArrayList<CalType> calTypes = new ArrayList<>();
    //顺序是反的

    public UnaryExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        unary = node;
    }

    public void checkError() {
        unary.checkError();
    }

    public DataType getDataType() {
        if (unary instanceof Exp) {
            return ((Exp) unary).getDataType();
        } else if (unary instanceof LVal) {
            return ((LVal) unary).getDataType();
        } else if (unary instanceof Number) {
            return DataType.INT;
        } else {
            assert unary instanceof FuncCall;
            return ((FuncCall) unary).getDataType();
        }
    }

    public int getValue() {
        //用于全局变量 编译时求值 运行过程中不会调用
        assert unary instanceof Exp || unary instanceof Number || unary instanceof LVal;
        int value = 0;
        if (unary instanceof Exp) {
            value = ((Exp) unary).getValue();
        } else if (unary instanceof Number){
            value = ((Number) unary).getValue();
        } else {
            ((LVal) unary).setDataType();
            assert unary instanceof LVal && ((LVal) unary).getDataType() == DataType.INT;
            ((LVal) unary).setIndex();
            SymbolTableEntry symbolTableEntry = MidCodeGener.getSymbolTable().
                    searchDefinedEntry(MidCodeGener.getFuncName(), unary.getName());
            if (((LVal) unary).getBrackNum() == 0) {
                value = symbolTableEntry.getValue();
            } else if (((LVal) unary).getBrackNum() == 1) {
                value = symbolTableEntry.getValue1D(((LVal) unary).getLength1D());
            } else {
                value = symbolTableEntry.getValue2D(((LVal) unary).getLength1D(),((LVal) unary).getLength2D());
            }
        }
        for (int i = 0;i < calTypes.size();i++) {
            //只可能是 + -
            assert calTypes.get(i) == CalType.add || calTypes.get(i) == CalType.sub;
            if (calTypes.get(i) == CalType.sub) {
                value *= -1;
            }
        }
        return value;
    }

    public void insertCaltype(String word) {
        assert word.equals("+") || word.equals("-") || word.equals("!");
        if (word.equals("+")) {
            calTypes.add(CalType.add);
        } else if (word.equals("-")) {
            calTypes.add(CalType.sub);
        } else {
            calTypes.add(CalType.not);
        }
    }

    @Override
    public String genMidCode() {
        int cntSub = 0;
        int cntNot = 0;
        for (CalType calType:calTypes) {
            if (calType == CalType.sub) {
                cntSub++;
            } else if (calType == CalType.not) {
                cntNot++;
            }
        }
        //计算是否需要取反
        String temp = unary.genMidCode();
        if (isNumber(temp)) {
            int value = Integer.valueOf(temp);
            if (cntSub % 2 != 0) {
                value = -value;
            }
            if (cntNot % 2 != 0) {
                if (value != 0) {
                    value = 0;
                } else {
                    value = 1;
                }
            }
            temp = Integer.toString(value);
        } else {
            if (cntSub % 2 != 0) {
                String dst = MidCodeGener.genTemp();
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
                                OpType.NEG,
                                temp,
                                null,
                                null,
                                dst
                        )
                );
                temp = dst;
            }
            if (cntNot % 2 != 0) {
                String dst = MidCodeGener.genTemp();
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
                                OpType.NOT,
                                temp,
                                null,
                                null,
                                dst
                        )
                );
                temp = dst;
            }
        }
        return temp;
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
