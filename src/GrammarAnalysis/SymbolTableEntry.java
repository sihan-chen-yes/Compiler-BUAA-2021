package GrammarAnalysis;

import Enum.DataType;
import Enum.DeclType;
import Enum.DimType;
import WordAnalysis.Word;

import java.util.ArrayList;

public class SymbolTableEntry {
    private String name;
    private DeclType declType;
    private DataType dataType;
    private DimType dimType;
    private Word Ident;
    private ArrayList<FParam> FParams;

    private ArrayList<Integer> paramsDim = new ArrayList<>();

    private int layer = 0;

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType,DimType dimType) {
        //声明
        this.name = Ident.getWord();
        this.declType = declType;
        this.dataType = dataType;
        this.dimType = dimType;
        this.Ident = Ident;
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType retType,ArrayList<FParam> FParams) {
        //函数声明
        this.name = Ident.getWord();
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = retType;
        this.FParams = FParams;
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType,DimType dimType, int layer) {
        //局部变量和常量声明
        this.name = Ident.getWord();
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = dataType;
        this.dimType = dimType;
        this.layer = layer;
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

    public ArrayList<FParam> getFParams() {
        return FParams;
    }
}
