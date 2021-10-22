import java.util.ArrayList;

public class SymbolTableEntry {
    private String name;
    private DeclType declType;
    private DataType dataType;
    private Word Ident;

    private ArrayList<Integer> paramsDim = new ArrayList<>();

    private int layer = -1;

    public SymbolTableEntry(Word Ident,DeclType declType,int brackNum) {
        //声明
        this.name = Ident.getWord();
        this.Ident = Ident;
        this.declType = declType;
        if (brackNum == 0) {
            this.dataType = DataType.INT;
        } else if (brackNum == 1) {
            dataType = DataType.ARRAY_1D;
        } else if (brackNum == 2) {
            dataType = DataType.ARRAY_2D;
        }
    }

    public SymbolTableEntry(Word Ident,DeclType declType,DataType dataType) {
        //函数声明
        this.name = Ident.getWord();
        this.Ident = Ident;
        this.declType = declType;
        this.dataType = dataType;
    }

    public SymbolTableEntry(Word Ident, DeclType declType,int brackNum, int layer) {
        //局部变量和常量
        this.name = Ident.getWord();
        this.Ident = Ident;
        this.declType = declType;
        if (brackNum == 0) {
            this.dataType = DataType.INT;
        } else if (brackNum == 1) {
            dataType = DataType.ARRAY_1D;
        } else if (brackNum == 2) {
            dataType = DataType.ARRAY_2D;
        }
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

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
