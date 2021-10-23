package ASTNode;
import Enum.*;
import java.util.ArrayList;

public class InitVal extends Node {
    private DimType dimType;
    private Node val = null;
    private ArrayList<Node> val_1D = null;
    private ArrayList<ArrayList<Node>> val_2D = null;

    public InitVal(int pos, DimType dimType) {
        super(pos);
        this.dimType = dimType;
        if (dimType == DimType.ARRAY_1D) {
            val_1D = new ArrayList<>();
        } else if (dimType == DimType.ARRAY_2D) {
            val_2D = new ArrayList<>();
        }
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (dimType == DimType.NOTARRAY) {
            val = node;
        } else if (dimType == DimType.ARRAY_1D) {
            val_1D.add(node);
        } else {

        }
    }
}
