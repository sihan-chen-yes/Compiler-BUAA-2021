package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import WordAnalysis.Word;
import Enum.*;
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

    public void checkError() {
        //print个数不匹配||存在不符合要求的字符
        if (Exps.size() != getPrintNum()) {
            ErrorAnalysis.addError(getLine(), ErrorType.printNumError);
        }
        if (illegalFormatString()) {
            ErrorAnalysis.addError(FormatString.getLine(), ErrorType.illegalString);
        }
        for (Node exp:Exps) {
            exp.checkError();
        }
    }

    public boolean illegalFormatString() {
        String formatString = FormatString.getWord();
        for (int i = 1;i < formatString.length() - 1;i++) {
            int ASCII = formatString.charAt(i);
            if ((formatString.charAt(i) == '\\' && formatString.charAt(i + 1) != 'n')
                    || (formatString.charAt(i) == '%' && formatString.charAt(i + 1) != 'd')
                    || (!(formatString.charAt(i) == '%' || formatString.charAt(i) == ' ' || formatString.charAt(i) == '!'
                    ||(ASCII >= 40 && ASCII <= 126)))) {
                return true;
            }
        }
        return false;
    }

    public int getPrintNum() {
        String formatString = FormatString.getWord();
        int count = 0;
        for (int i = 1;i < formatString.length() - 1;i++) {
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                count++;
            }
        }
        return count;
    }
}
