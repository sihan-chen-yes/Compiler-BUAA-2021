package ASTNode;
import Enum.*;
import WordAnalysis.Word;

public class VarDef extends Node {
    private DimType dimType;
    private InitVal InitVal = null;

    public VarDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        InitVal = (InitVal) node;
    }

    public void setDimType(int dim) {
        if (dim == 0) {
            dimType = DimType.NOTARRAY;
        } else if (dim == 1) {
            dimType = DimType.ARRAY_1D;
        } else {
            dimType = DimType.ARRAY_2D;
        }
    }

    public DimType getDimType() {
        return dimType;
    }
}
