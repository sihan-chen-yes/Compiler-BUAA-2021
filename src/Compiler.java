import java.io.*;
import java.util.*;

public class Compiler {
    public static void main(String[] args) {
        File readFile = new File("testfile.txt");
        File writeFile = new File("output.txt");
        ArrayList<HashMap<String, String>> wordAndGrammarList = null;
        HashMap<String, String> symbol;
        try {
            RandomAccessFile readRaf = new RandomAccessFile(readFile, "r");
            WordAnalysis wordAnalysis = new WordAnalysis(readRaf);
            GrammarAnalysis grammarAnalysis = new GrammarAnalysis(wordAnalysis);
            wordAndGrammarList = grammarAnalysis.recursionDown();
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            FileWriter writer = new FileWriter(writeFile);
            Iterator iterator = wordAndGrammarList.iterator();
            while (iterator.hasNext()) {
                symbol = (HashMap<String, String>) iterator.next();
                if (symbol.get("class").equals("NONTER")) {
                    writer.write(String.format("%s\n",symbol.get("word")));
                } else {
                    writer.write(String.format("%s %s\n",symbol.get("class"),symbol.get("word")));
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
