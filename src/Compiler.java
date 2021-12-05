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
    public static boolean op = true;
    public static void main(java.lang.String[] args) {
        boolean debug = false;
        debug = true;
        //本地debug模式
        //是否开启优化

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
            //中间代码生成
            if (!op) {
                midCodeGener.saveMidCode();
                System.out.println("genMidcode OK\n");
                TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile,debug);
                targetCodeGener.saveTargetCode();
                System.out.println("genTargetCode OK\n");
            } else {
                if (debug) {
                    midCodeGener.saveMidCode();
                }
                System.out.println("genMidcode OK\n");
                Optimizer optimizer = new Optimizer();
                optimizer.optimize();
                midCodeGener.saveOpMidCode();
                System.out.println("optimize midcode OK\n");
                TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile,debug);
                if (debug) {
                    targetCodeGener.saveTargetCode();
                }
                System.out.println("genTargetCode OK\n");
                targetCodeGener.saveOpTarCode();
                System.out.println("optimize tarcode OK\n");
            }
        }
        if (debug) {
            MidCodeGener.getSymbolTable().saveSymbleTable();
            //打印符号表
        }
    }

    public static boolean isOp() {
        return op;
    }
}
