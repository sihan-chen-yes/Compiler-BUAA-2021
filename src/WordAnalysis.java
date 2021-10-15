import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class WordAnalysis {
    private final RandomAccessFile raf;
    private int sym;
    private char c;
    private String token = "";
    private final int EOF = -1;
    private final HashMap<String, String> reservedWords = new HashMap<>();
    private int line = 1;
    private ArrayList<HashMap<String, String>> bufferWords = new ArrayList<>();

    public WordAnalysis (RandomAccessFile raf){
        this.raf = raf;
        reservedWords.put("main","MAINTK");
        reservedWords.put("const","CONSTTK");
        reservedWords.put("int","INTTK");
        reservedWords.put("break","BREAKTK");
        reservedWords.put("continue","CONTINUETK");
        reservedWords.put("if","IFTK");
        reservedWords.put("else","ELSETK");
        reservedWords.put("while","WHILETK");
        reservedWords.put("getint","GETINTTK");
        reservedWords.put("printf","PRINTFTK");
        reservedWords.put("return","RETURNTK");
        reservedWords.put("void","VOIDTK");
    }

    public HashMap<String, String> peekSymbol(int i) {
        while (i >= bufferWords.size()) {
            HashMap<String, String> word = getSymbol(true);
            while (word.get("class").equals("ANNO")) {
                word = getSymbol(true);
            }
            //过滤注释
            bufferWords.add(word);
        }
        return bufferWords.get(i);
    }

    public HashMap<String, String> getSymbol(boolean peeking){
        if (!bufferWords.isEmpty() && !peeking) {
            HashMap<String, String> word = bufferWords.get(0);
            bufferWords.remove(0);
            return word;
        }
        token = "";
        HashMap<String, String> word = new HashMap<>();
        try {
            jumpBlank();//每次进来先检查一下上次的暂停字符 读一个字符并且跳过空白符 再检查一下是不是EOF
        } catch (ReadEOFException e) {
            word.put("class","EOF");
            word.put("word",token);
            return word;
        }
        if (Character.isLetter(c) || c == '_') {
            //下划线或者字母开头 后面可以是下划线字母或者数字
            while ((Character.isLetterOrDigit(c) || c == '_' ) && sym != EOF) {
                token += c;
                getChar();
            }
            if (sym != EOF) {
                retract();
            }
            if (reservedWords.containsKey(token)) {
                word.put("class",reservedWords.get(token));
                word.put("word",token);
            } else {
                word.put("class","IDENFR");
                word.put("word",token);
            }
        } else if (Character.isDigit(c)) {
            //0或者非零的数（不能以0开头）
            while (Character.isDigit(c) && sym != EOF) {
                token += c;
                getChar();
            }
            if (sym != EOF) {
                retract();
            }
            word.put("class","INTCON");
            word.put("word",token);
        } else if (c == '"') {
            do {
                token += c;
                getChar();
            } while (c != '"');
            token += c;
            word.put("class","STRCON");
            word.put("word",token);
        } else if (c == '+') {
            token += c;
            word.put("class","PLUS");
            word.put("word",token);
        } else if (c == '-') {
            token += c;
            word.put("class","MINU");
            word.put("word",token);
        } else if (c == '*') {
            token += c;
            word.put("class","MULT");
            word.put("word",token);
        } else if (c == '%') {
            token += c;
            word.put("class","MOD");
            word.put("word",token);
        } else if (c == ';') {
            token += c;
            word.put("class","SEMICN");
            word.put("word",token);
        } else if (c == ',') {
            token += c;
            word.put("class","COMMA");
            word.put("word",token);
        } else if (c == '(') {
            token += c;
            word.put("class","LPARENT");
            word.put("word",token);
        } else if (c == ')') {
            token += c;
            word.put("class","RPARENT");
            word.put("word",token);
        } else if (c == '[') {
            token += c;
            word.put("class","LBRACK");
            word.put("word",token);
        } else if (c == ']') {
            token += c;
            word.put("class","RBRACK");
            word.put("word",token);
        } else if (c == '{') {
            token += c;
            word.put("class","LBRACE");
            word.put("word",token);
        } else if (c == '}') {
            token += c;
            word.put("class","RBRACE");
            word.put("word",token);
        } else if (c == '&') {
            token += c;
            getChar();
            assert c == '&';
            token += c;
            word.put("class","AND");
            word.put("word",token);
        } else if (c == '|') {
            token += c;
            getChar();
            assert c == '|';
            token += c;
            word.put("class","OR");
            word.put("word",token);
        } else if (c == '!') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word.put("class","NEQ");
            } else {
                retract();
                word.put("class","NOT");
            }
            word.put("word",token);
        } else if (c == '<') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word.put("class","LEQ");
            } else {
                retract();
                word.put("class","LSS");
            }
            word.put("word",token);
        } else if (c == '>') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word.put("class","GEQ");
            } else {
                retract();
                word.put("class","GRE");
            }
            word.put("word",token);
        } else if (c == '=') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word.put("class","EQL");
            } else {
                retract();
                word.put("class","ASSIGN");
            }
            word.put("word",token);
        } else if (c == '/') {
            token += c;
            getChar();
            if (c == '*') {
                while (true) {
                    do {
                        token += c;
                        getChar();
                    } while (c != '*');
                    char pre = c;
                    getChar();
                    if (c == '/') {
                        token += pre;
                        token += c;
                        word.put("class","ANNO");
                        word.put("word",token);
                        break;
                    } else {
                        retract();
                        c = pre;
                    }
                }
            } else if (c == '/') {
                do {
                    token += c;
                    getChar();
                } while (c != '\n' && sym != EOF);
                word.put("class","ANNO");
                word.put("word",token);
            } else {
                retract();
                word.put("class","DIV");
                word.put("word",token);
            }
        }
        return word;
    }

    public void jumpBlank() throws ReadEOFException {
        judgeEnd();
        do {
            if (c == '\n') {
                line++;
            }
            getChar();
        } while (c == '\n' || c == '\r' || c == '\t' || c == ' ');
        judgeEnd();
    }

    public void judgeEnd() throws ReadEOFException {
        if (sym == EOF) {
            throw new ReadEOFException();
        }
    }

    public void getChar(){
        try {
            sym = raf.read();
            c = (char) sym;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void retract(){
        try {
            raf.seek(raf.getFilePointer() - 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
