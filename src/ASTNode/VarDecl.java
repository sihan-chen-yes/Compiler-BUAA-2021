package ASTNode;

import Enum.DataType;
import Enum.DeclType;

import java.util.ArrayList;

public class VarDecl extends Node {
    private DeclType declType = DeclType.VAR;
    private BType BType = null;
    private ArrayList<VarDef> VarDefs = new ArrayList<>();

    public VarDecl(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof BType) {
            BType = (BType) node;
        } else if (node instanceof VarDef) {
            VarDefs.add((VarDef) node);
            ((VarDef) node).setDataType(getDataType());
        }
    }

    public DeclType getDeclType() {
        return declType;
    }

    public DataType getDataType() {
        return BType.getDataType();
    }

    public void checkError() {
        for (VarDef varDef:VarDefs) {
            varDef.checkError();
        }
    }

    @Override
    public String genMidCode() {
        for (VarDef varDef:VarDefs) {
            varDef.genMidCode();
        }
        return super.genMidCode();
    }
}
