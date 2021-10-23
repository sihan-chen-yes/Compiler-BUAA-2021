package ASTNode;

import Enum.DataType;
public class FuncType extends Node {
    private DataType dataType;

    public FuncType(int pos,DataType dataType) {
        super(pos);
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }
}
