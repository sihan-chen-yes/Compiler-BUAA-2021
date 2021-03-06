package ASTNode;
import WordAnalysis.Word;

public abstract class Node {
    private Word word = null;
    //非终结符没有word
    private Node father = null;
    int pos;

    public Node(Word word, int pos) {
        this.word = word;
        this.pos = pos;
    }

    public Node(int pos) {
        this.pos = pos;
    }

    public void link(Node node) {
        node.setFather(this);
    }

    public void setFather(Node node) {
        father = node;
    }

    public Node getFather() {
        return father;
    }

    public int getLine() {
        return word.getLine();
    }

    public Word getWord() {
        return word;
    }

    public void checkError() {

    }

    public String genMidCode() {
        return "";
    }

    public java.lang.String getName() {
        return word.getWord();
    }
}
