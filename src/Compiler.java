import ASTNode.CompUnit;
import GrammarAnalysis.ErrorAnalysis;
import GrammarAnalysis.GrammarAnalysis;
import MidCodeGeneration.MidCodeGener;
import Optimizer.Optimizer;
import TargetCodeGeneration.TargetCodeGener;
import WordAnalysis.Word;
import WordAnalysis.WordAnalysis;

import java.io.File;
import java.util.ArrayList;

public class Compiler {
    public static void main(java.lang.String[] args) {
        boolean debug = false;
//        debug = true;

        File readFile = new File("testfile.txt");
        File outputFile = new File("output.txt");
        File errorFile = new File("error.txt");
        File midCodeFile = new File("midcode.txt");
        File targetCodeFile = new File("mips.txt");
        WordAnalysis wordAnalysis = new WordAnalysis(readFile);
        ArrayList<Word> wordList = wordAnalysis.getWordList();
        //词法分析
        ErrorAnalysis errorAnalysis = new ErrorAnalysis(errorFile);

        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordList,outputFile,errorAnalysis);
        grammarAnalysis.recursionDown();
//        grammarAnalysis.saveGrammarAnalysis();
        //语法分析

        CompUnit root = (CompUnit) grammarAnalysis.getASTroot();
        root.checkError();
        //错误处理

        if (errorAnalysis.findErrors()) {
            errorAnalysis.saveErrorAnalysis();
            System.out.println("find errors!");
        } else {
            MidCodeGener midCodeGener = new MidCodeGener(midCodeFile,debug);
            root.genMidCode();
            System.out.println("genMidcode OK\n");
            if (debug) {
                midCodeGener.saveMidCode();
            }
            //中间代码生成
            Optimizer optimizer = new Optimizer();
            optimizer.optimize();
            midCodeGener.saveOpMidCode();
            System.out.println("optimize midcode OK\n");

            TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile,debug);
            if (debug) {
                targetCodeGener.saveTargetCode();
            }
            System.out.println("genTargetCode OK\n");
            //目标代码生成
            targetCodeGener.saveOpTarCode();
            System.out.println("optimize tarcode OK\n");
        }
        if (debug) {
            MidCodeGener.getSymbolTable().saveSymbleTable();
            //打印符号表
        }
    }
}
