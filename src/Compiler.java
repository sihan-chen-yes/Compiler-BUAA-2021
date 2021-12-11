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
    private static boolean op = true;
//    private static boolean op = false;
    //优化开关
    // 影响while
    // 影响sregs


    private static boolean debug = true;
//    private static boolean debug = false;
    //debug开关

    public static void main(java.lang.String[] args) {
        Optimizer.setDebug(debug);
        //本地debug模式
        Optimizer.setOp(op);
        //优化开关~

        File readFile = new File("testfile.txt");
        File outputFile = new File("output.txt");
        File errorFile = new File("error.txt");
        File midCodeFile = new File("midcode.txt");
        File targetCodeFile = new File("mips.txt");
        File opMidCodeFile = new File("opmidcode.txt");
        File opTargetCodeFile = new File("opmips.txt");
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
            MidCodeGener midCodeGener = new MidCodeGener(midCodeFile,opMidCodeFile);
            root.genMidCode();
            //中间代码生成
            if (!op) {
                //未开启优化
                midCodeGener.saveMidCode();
//                System.out.println("genMidcode OK\n");
                TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile,opTargetCodeFile);
                targetCodeGener.saveTargetCode();
//                System.out.println("genTargetCode OK\n");
            } else {
                if (debug) {
                    midCodeGener.saveMidCode();
                }
//                System.out.println("genMidcode OK\n");
                Optimizer optimizer = new Optimizer();
                optimizer.optimize();
                midCodeGener.saveOpMidCode();
//                System.out.println("opMidcode OK\n");
                TargetCodeGener targetCodeGener = new TargetCodeGener(targetCodeFile,opTargetCodeFile);
                if (debug) {
                    targetCodeGener.saveTargetCode();
                }
//                System.out.println("genTargetCode OK\n");
                targetCodeGener.saveOpTarCode();
//                System.out.println("opTargetCode OK\n");
            }
        }
        if (debug) {
            MidCodeGener.getSymbolTable().saveSymbleTable();
//            System.out.println("genSymbolTable OK\n");
            //打印符号表
        }
    }

}
