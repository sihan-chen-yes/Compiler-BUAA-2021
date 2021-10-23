package ASTNode;

import WordAnalysis.Word;

import java.util.ArrayList;

public class FuncCall extends Node {
    private ArrayList<Node> FuncRParams = new ArrayList<>();

    public FuncCall(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        FuncRParams.add(node);
    }

    public ArrayList<Node> getFuncRParams() {
        return FuncRParams;
    }
}
