package ASTNode;
import Enum.*;
public class BType extends Node {
    private DataType dataType;

    public BType(int pos,DataType dataType) {
        super(pos);
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }
}
