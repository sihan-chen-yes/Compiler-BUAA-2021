package GrammarAnalysis;

import ASTNode.FuncFParam;
import Enum.DataType;
import Enum.DeclType;
import WordAnalysis.Word;

import java.util.ArrayList;

public class SymbolTableEntry {
    private DeclType declType;
    private DataType dataType;
    private Word Ident;
    private String name;
    private ArrayList<FuncFParam> FParams;

    private int offset_gp;
    private int offset_sp;

    private int value;
    private ArrayList<Integer> values1D;
    private ArrayList<ArrayList<Integer>> values2D;
    private int length1D;
    private int length2D;
    private int size;

    private ArrayList<Integer> paramsDim = new ArrayList<>();

    private int layer = 0;

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType) {
        //声明
        this.declType = declType;
        this.dataType = dataType;
        this.Ident = Ident;
        name = Ident.getWord();
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType retType,ArrayList<FuncFParam> FParams) {
        //函数声明
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = retType;
        this.FParams = FParams;
        name = Ident.getWord();
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType, int layer) {
        //局部变量和常量声明
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = dataType;
        this.layer = layer;
        name = Ident.getWord();
    }

    public String getName() {
        return name;
    }

    public DeclType getDeclType() {
        return declType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public int getLayer() {
        return layer;
    }

    public int getLine() {
        return Ident.getLine();
    }

    public int getFParamNum() {
        return FParams.size();
    }

    public ArrayList<FuncFParam> getFParams() {
        return FParams;
    }

    public int getOffset_gp() {
        return offset_gp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public ArrayList<Integer> getValues1D() {
        return values1D;
    }

    public void setValues1D(ArrayList<Integer> values1D) {
        this.values1D = values1D;
    }

    public ArrayList<ArrayList<Integer>> getValues2D() {
        return values2D;
    }

    public void setValues2D(ArrayList<ArrayList<Integer>> values2D) {
        this.values2D = values2D;
    }

    public int getLength1D() {
        return length1D;
    }

    public void setLength1D(int length1D) {
        this.length1D = length1D;
    }

    public int getLength2D() {
        return length2D;
    }

    public void setLength2D(int length2D) {
        this.length2D = length2D;
    }

    public int getValue1D(int dim1) {
        return values1D.get(dim1);
    }

    public int getValue2D(int dim1,int dim2) {
        return values2D.get(dim1).get(dim2);
    }

    public void setSize() {
        if (declType == DeclType.PARAM) {
            //是参数
            if (dataType == DataType.INT || dataType == DataType.INT_ARRAY_1D || dataType == DataType.INT_ARRAY_2D) {
                size = 4;
            }
        } else {
            //不是参数
            if (dataType == DataType.INT) {
                size = 4;
            } else if (dataType == DataType.INT_ARRAY_1D) {
                size = length1D * 4;
            } else {
                size = length1D * length2D * 4;
            }
        }
    }

    public void setgpAddr() {
        offset_gp = SymbolTable.getOffset_gp();
        SymbolTable.setOffset_gp(offset_gp + size);
    }

    public void setspAddr() {
        offset_sp = SymbolTable.getOffset_sp();
        SymbolTable.setOffset_sp(offset_sp + size);
    }

    public int getSize() {
        return size;
    }

    public void refactorName() {
        name = getName() + Integer.toString(getLine());
    }

}
