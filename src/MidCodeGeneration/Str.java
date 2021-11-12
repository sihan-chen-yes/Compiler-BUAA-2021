package MidCodeGeneration;

public class Str {
    private String content;
    private String strIdent;

    public Str(String content, String strIdent) {
        this.content = content;
        this.strIdent = strIdent;
    }

    @Override
    public String toString() {
        return String.format("%s:%s",strIdent,content);
    }
}
