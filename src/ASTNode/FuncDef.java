package ASTNode;
import Enum.*;
import WordAnalysis.Word;

import java.util.ArrayList;

public class FuncDef extends Node {
    private FuncType funcType;
    private FuncFParams funcFParams = null;
    private Block block;

    public FuncDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof FuncType) {
            funcType = (FuncType) node;
        } else if (node instanceof FuncFParams){
            funcFParams = (FuncFParams) node;
        } else {
            block = (Block) node;
        }
    }

    public DataType getDataType() {
        return funcType.getDataType();
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        if (funcFParams == null) {
            return new ArrayList<>();
        }
        return funcFParams.getFuncFParams();
    }

    public Node getBlock() {
        return block;
    }
}
