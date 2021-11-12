package ASTNode;

import WordAnalysis.Word;

public class Number extends Node {
    public Number(Word word, int pos) {
        super(word,pos);
    }

    public int getValue() {
        return Integer.parseInt(getWord().getWord());
    }

    @Override
    public String genMidCode() {
        return getWord().getWord();
    }
}
