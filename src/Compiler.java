import ASTNode.CompUnit;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.GrammarAnalysis;
import MidCodeGeneration.MidCodeGener;
import TargetCodeGeneration.TargetCodeGener;
import WordAnalysis.Word;
import WordAnalysis.WordAnalysis;

import java.io.File;
import java.util.ArrayList;

public class Compiler {
    public static void main(java.lang.String[] args) {
        File readFile = new File("testfile.txt");
        File outputFile = new File("output.txt");
        File errorFile = new File("error.txt");
        File midCodeFile = new File("midcode.txt");
        File targetCodeFile = new File("mips.txt");
        WordAnalysis wordAnalysis = new WordAnalysis(readFile);
        ArrayList<Word> wordList = wordAnalysis.getWordList();
        ErrorAnalysis errorAnalysis = new ErrorAnalysis(errorFile);
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordList,outputFile,errorAnalysis);
        grammarAnalysis.recursionDown();
//        grammarAnalysis.saveGrammarAnalysis();
        CompUnit root = (CompUnit) grammarAnalysis.getASTroot();
        root.checkError();
        if (errorAnalysis.findErrors()) {
            errorAnalysis.saveErrorAnalysis();
            System.out.println("find errors!");
        } else {
            MidCodeGener midCodeGener = new MidCodeGener(midCodeFile);
            root.genMidCode();
            System.out.println("genMidcode OK\n");
            midCodeGener.saveMidCode();
            TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile);
            targetCodeGener.saveTargetCode();
            System.out.println("genTargetCode OK\n");
        }
    }
}
