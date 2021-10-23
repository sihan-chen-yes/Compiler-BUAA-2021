package ASTNode;

import java.util.ArrayList;

public class FuncRParams extends Node {
    private ArrayList<FuncRParam> funcRParams = new ArrayList<>();

    public FuncRParams(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        funcRParams.add((FuncRParam) node);
    }

    public void checkError() {
        for (FuncRParam funcRParam:funcRParams) {
            funcRParam.checkError();
        }
    }

    public int getFuncRParmaNum() {
        return funcRParams.size();
    }

    public ArrayList<FuncRParam> getFuncRParams() {
        return funcRParams;
    }
}
