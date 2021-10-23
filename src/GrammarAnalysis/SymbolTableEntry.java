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
    private ArrayList<FuncFParam> FParams;

    private ArrayList<Integer> paramsDim = new ArrayList<>();

    private int layer = 0;

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType) {
        //声明
        this.declType = declType;
        this.dataType = dataType;
        this.Ident = Ident;
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType retType,ArrayList<FuncFParam> FParams) {
        //函数声明
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = retType;
        this.FParams = FParams;
    }

    public SymbolTableEntry(Word Ident, DeclType declType, DataType dataType, int layer) {
        //局部变量和常量声明
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = dataType;
        this.layer = layer;
    }

    public String getName() {
        return Ident.getWord();
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
}
