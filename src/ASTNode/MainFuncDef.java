package ASTNode;


import WordAnalysis.Word;

public class MainFuncDef extends Node {
    private Node Block;

    public MainFuncDef(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Block = node;
    }
}
