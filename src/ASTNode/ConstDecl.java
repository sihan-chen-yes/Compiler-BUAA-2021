package ASTNode;

import Enum.DataType;
import Enum.DeclType;

import java.util.ArrayList;

public class ConstDecl extends Node {
    private DeclType declType = DeclType.CONST;
    private BType BType = null;
    private ArrayList<ConstDef> ConstDefs = new ArrayList<>();
    public ConstDecl(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof BType) {
            BType = (BType) node;
        } else {
            ConstDefs.add((ConstDef) node);
            ((ConstDef) node).setDataType(getDataType());
        }
    }

    public DeclType getDeclType() {
        return declType;
    }

    public DataType getDataType() {
        return BType.getDataType();
    }

    public void checkError() {
        for (Node constDef:ConstDefs) {
            constDef.checkError();
        }
    }
}
