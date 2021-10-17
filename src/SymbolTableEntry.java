import java.util.ArrayList;

public class SymbolTableEntry {
    private String name;
    private DeclType declType;
    private int dim1 = 0;
    private int dim2 = 0;

    private RetType retType;
    private ArrayList<String> params = new ArrayList<>();

    private int layer = 0;

    public SymbolTableEntry(String name, DeclType declType, int dim1, int dim2) {
        this.name = name;
        this.declType = declType;
        this.dim1 = dim1;
        this.dim2 = dim2;
    }

    public SymbolTableEntry(String name, RetType retType, ArrayList<String> params) {
        this.name = name;
        this.retType = retType;
        this.params = params;
    }

    public SymbolTableEntry(String name, int layer) {
        this.name = name;
        this.layer = layer;
    }

    public String getName() {
        return name;
    }

    public DeclType getDeclType() {
        return declType;
    }

    public int getDim1() {
        return dim1;
    }

    public int getDim2() {
        return dim2;
    }

    public RetType getRetType() {
        return retType;
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public int getLayer() {
        return layer;
    }
}
