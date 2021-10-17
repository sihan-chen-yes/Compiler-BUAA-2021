import java.io.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) {
        File readFile = new File("testfile.txt");
        File outputFile = new File("output.txt");
        WordAnalysis wordAnalysis = new WordAnalysis(readFile);
        ArrayList<Word> wordList = wordAnalysis.getWordList();
//        wordAnalysis.print();
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordList,outputFile);
        grammarAnalysis.recursionDown();
        grammarAnalysis.saveGrammarAnalysis();
    }
}
