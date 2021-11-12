package ASTNode;

import GrammarAnalysis.ErrorAnalysis;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import WordAnalysis.Word;
import Enum.*;
import java.util.ArrayList;

public class PrintStmt extends Node {
    private Word formatWord;
    ArrayList<Node> Exps = new ArrayList<>();

    private int cnt = 0;

    public PrintStmt(Word word, int pos) {
        super(word,pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        Exps.add(node);
    }

    public void addFormatString(Word word) {
        formatWord = word;
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
            ErrorAnalysis.addError(formatWord.getLine(), ErrorType.illegalString);
        }
        for (Node exp:Exps) {
            exp.checkError();
        }
    }

    public boolean illegalFormatString() {
        String formatString = formatWord.getWord();
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
        String formatString = formatWord.getWord();
        int count = 0;
        for (int i = 1;i < formatString.length() - 1;i++) {
            if (formatString.charAt(i) == '%' && formatString.charAt(i + 1) == 'd') {
                count++;
            }
        }
        return count;
    }

    @Override
    public String genMidCode() {
        String formatStr = formatWord.getWord().substring(1,formatWord.getWord().length() - 1);
        for (String str:formatStr.split("%d")) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.PRINT_STRING,
                            null,null,null,MidCodeGener.genStr(str)
                    )
            );
        }
        if (cnt < Exps.size()) {
            genPrintInt();
        }
        return super.genMidCode();
    }

    public void genPrintInt() {
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(OpType.PRINT_INT,null,null,null,Exps.get(cnt).genMidCode()));
        cnt++;
    }
}
