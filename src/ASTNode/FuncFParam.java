package ASTNode;

import Enum.DataType;
import WordAnalysis.Word;

import java.util.ArrayList;

public class FuncFParam extends Node {
    private BType bType;
    private ArrayList<ConstExp> constExps = new ArrayList<>();
    private DataType dataType;

    public FuncFParam(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof BType) {
            bType = (BType) node;
        } else {
            constExps.add((ConstExp) node);
        }
    }

    public DataType getDataType() {
        return dataType;
    }


    public void setDimType(int dim) {
        if (dim == 0) {
            dataType = DataType.INT;
        } else if (dim == 1) {
            dataType = DataType.INT_ARRAY_1D;
        } else {
            dataType = DataType.INT_ARRAY_2D;
        }
    }

    public int getLength2D() {
        assert dataType == DataType.INT_ARRAY_2D && !constExps.isEmpty();
        return constExps.get(0).getValue();
    }
}
