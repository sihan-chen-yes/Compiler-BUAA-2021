package ASTNode;

import WordAnalysis.Word;

import java.util.ArrayList;

public class PrintStmt extends Node {
    private Word FormatString;
    ArrayList<Node> Exps = new ArrayList<>();

    public PrintStmt(Word word,int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Exps.add(node);
    }

    public void addFormatString(Word word) {
        FormatString = word;
    }

    public ArrayList<Node> getExps() {
        return Exps;
    }
}
