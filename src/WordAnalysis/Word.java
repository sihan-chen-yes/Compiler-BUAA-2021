package WordAnalysis;

public class Word {
    private String className;
    private String word;
    private int line;

    public Word(String className, String word, int line) {
        this.className = className;
        this.word = word;
        this.line = line;
    }

    public String getClassName() {
        return className;
    }

    public String getWord() {
        return word;
    }

    public int getLine() {
        return line;
    }
}
