package ASTNode;

import java.util.ArrayList;

public class FuncFParams extends Node {
    private ArrayList<FuncFParam> funcFParams = new ArrayList<>();

    public FuncFParams(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        funcFParams.add((FuncFParam) node);
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }
}
