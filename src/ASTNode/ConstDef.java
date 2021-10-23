package ASTNode;
import Enum.*;
import WordAnalysis.Word;

public class ConstDef extends Node {
    private DimType dimType;
    private ConstInitVal ConstInitVal = null;

    public ConstDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        ConstInitVal = (ConstInitVal) node;
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
