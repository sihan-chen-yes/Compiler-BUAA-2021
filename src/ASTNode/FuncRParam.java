package ASTNode;
import Enum.DataType;
import Enum.DimType;

public class FuncRParam extends Node {
    private DataType dataType = DataType.INT;
    private DimType dimType;
    private Exp exp;

    public FuncRParam(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        assert node instanceof Exp;
        exp = (Exp) node;
    }

    public DataType getDataType() {
        return dataType;
    }

    public DimType getDimType() {
        return dimType;
    }

    public void setDimType(DimType dimType) {
        this.dimType = dimType;
    }
}
