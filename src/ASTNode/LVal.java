package ASTNode;

import WordAnalysis.Word;

import java.util.ArrayList;

public class LVal extends Node {
    private ArrayList<Exp> exps = new ArrayList<>();

    public LVal(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
    }

    public int getBrackNum() {
        return exps.size();
    }
}
