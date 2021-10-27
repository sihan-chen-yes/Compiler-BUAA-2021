package WordAnalysis;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

public class WordAnalysis {
    private RandomAccessFile raf = null;
    private int sym;
    private char c;
    private String token = "";
    private final int EOF = -1;
    private final HashMap<String, String> reservedWords = new HashMap<>();
    private int line = 1;
    private ArrayList<Word> wordList = new ArrayList<>();

    public WordAnalysis (File readFile){
        try {
            this.raf = new RandomAccessFile(readFile, "r");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public ArrayList<Word> getWordList() {
        Word word;
        do {
            word = getSymbol();
            if (!word.getClassName().equals("EOF") && !word.getClassName().equals("ANNO")) {
                wordList.add(word);
            }
        } while (!word.getClassName().equals("EOF"));
        return wordList;
    }

    public Word getSymbol(){
        token = "";
        Word word = null;
        try {
            jumpBlank();//每次进来先检查一下上次的暂停字符 读一个字符并且跳过空白符 再检查一下是不是EOF
        } catch (ReadEOFException e) {
            word = new Word("EOF",token,line);
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
                word = new Word(reservedWords.get(token),token,line);
            } else {
                word = new Word("IDENFR",token,line);
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
            word = new Word("INTCON",token,line);
        } else if (c == '"') {
            do {
                token += c;
                getChar();
            } while (c != '"');
            token += c;
            word = new Word("STRCON",token,line);
        } else if (c == '+') {
            token += c;
            word = new Word("PLUS",token,line);
        } else if (c == '-') {
            token += c;
            word = new Word("MINU",token,line);
        } else if (c == '*') {
            token += c;
            word = new Word("MULT",token,line);
        } else if (c == '%') {
            token += c;
            word = new Word("MOD",token,line);
        } else if (c == ';') {
            token += c;
            word = new Word("SEMICN",token,line);
        } else if (c == ',') {
            token += c;
            word = new Word("COMMA",token,line);
        } else if (c == '(') {
            token += c;
            word = new Word("LPARENT",token,line);
        } else if (c == ')') {
            token += c;
            word = new Word("RPARENT",token,line);
        } else if (c == '[') {
            token += c;
            word = new Word("LBRACK",token,line);
        } else if (c == ']') {
            token += c;
            word = new Word("RBRACK",token,line);
        } else if (c == '{') {
            token += c;
            word = new Word("LBRACE",token,line);
        } else if (c == '}') {
            token += c;
            word = new Word("RBRACE",token,line);
        } else if (c == '&') {
            token += c;
            getChar();
            assert c == '&';
            token += c;
            word = new Word("AND",token,line);
        } else if (c == '|') {
            token += c;
            getChar();
            assert c == '|';
            token += c;
            word = new Word("OR",token,line);
        } else if (c == '!') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word = new Word("NEQ",token,line);
            } else {
                retract();
                word = new Word("NOT",token,line);
            }
        } else if (c == '<') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word = new Word("LEQ",token,line);
            } else {
                retract();
                word = new Word("LSS",token,line);
            }
        } else if (c == '>') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word = new Word("GEQ",token,line);
            } else {
                retract();
                word = new Word("GRE",token,line);
            }
        } else if (c == '=') {
            token += c;
            getChar();
            if (c == '=') {
                token += c;
                word = new Word("EQL",token,line);
            } else {
                retract();
                word = new Word("ASSIGN",token,line);
            }
        } else if (c == '/') {
            token += c;
            getChar();
            if (c == '*') {
                //注释开始 注释中可能有\n
                while (true) {
                    do {
                        token += c;
                        getChar();
                        setLine();
                    } while (c != '*');
                    char pre = c;//为 *
                    getChar();//预读 * 之后的字符
                    if (c == '/') {
                        token += pre;
                        token += c;
                        word = new Word("ANNO",token,line);
                        break;
                    } else {
                        retract();
                        c = pre;
                        //不是/ 注释还没有结束 回退再找下一个 *
                    }
                }
            } else if (c == '/') {
                do {
                    token += c;
                    getChar();
                    setLine();//出循环的时候已经读了\n 需要设置行号
                } while (c != '\n' && sym != EOF);
                word = new Word("ANNO",token,line);
            } else {
                retract();
                word = new Word("DIV",token,line);
            }
        }
        return word;
    }

    public void jumpBlank() throws ReadEOFException {
        judgeEnd();
        do {
            getChar();
            setLine();
        } while (c == '\n' || c == '\r' || c == '\t' || c == ' ');
        judgeEnd();
    }

    public void setLine() {
        if (c == '\n') {
            line++;
        }
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

    public void print() {
        //debug
        for (Word word:
                wordList) {
            System.out.println(word.getWord());
        }
    }
}
