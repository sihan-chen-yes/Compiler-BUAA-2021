package ASTNode;

import Enum.DataType;
import Enum.DimType;
import WordAnalysis.Word;

public class FuncFParam extends Node {
    private BType bType;
    private DimType dimType;

    public FuncFParam(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        bType = (BType) node;
    }

    public DataType getDataType() {
        return bType.getDataType();
    }

    public DimType getDimType() {
        return dimType;
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
}
