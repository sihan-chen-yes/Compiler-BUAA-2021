import ASTNode.*;
import GrammarAnalysis.*;
import WordAnalysis.*;

import java.io.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) {
        File readFile = new File("testfile.txt");
        File outputFile = new File("output.txt");
        File errorFile = new File("error.txt");
        WordAnalysis wordAnalysis = new WordAnalysis(readFile);
        ArrayList<Word> wordList = wordAnalysis.getWordList();
//        wordAnalysis.print();
        ErrorAnalysis errorAnalysis = new ErrorAnalysis(errorFile);
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordList,outputFile,errorAnalysis);
        grammarAnalysis.recursionDown();
//        grammarAnalysis.saveGrammarAnalysis();
        CompUnit root = (CompUnit) grammarAnalysis.getASTroot();
        root.checkError();
        errorAnalysis.saveErrorAnalysis();
    }
}
