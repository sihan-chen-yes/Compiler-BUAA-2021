package MidCodeGeneration;

import Enum.OpType;
import GrammarAnalysis.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MidCodeGener {
    private FileWriter writer;
    private FileWriter opWriter;
    private static ArrayList<MidCodeEntry> global = new ArrayList<>();
    private static ArrayList<MidCodeEntry> midCodeList = new ArrayList<>();
    private static ArrayList<Str> strList = new ArrayList<>();
    private static SymbolTable symbolTable = new SymbolTable();

    private static int layer = 0;

    private static String funcName = null;

    private static int temp_num = 0;
    private static int str_num = 0;
    private static int label_num = 0;
    //距离直接调用函数的stack大小

    private String globalStart = "########################################GLOBAL START########################################\n";
    private String globalEnd = "########################################GLOBAL END##########################################\n";
    private String asciizStart = "########################################ASCIIZ START########################################\n";
    private String asciizEnd = "########################################ASCIIZ END##########################################\n";


    private static ArrayList<MidCodeEntry> opMidCodeList = new ArrayList<>();

    public MidCodeGener(File midcodeFile,boolean debug) {
        try {
            this.writer = new FileWriter(midcodeFile);
            File file;
            if (debug) {
                file = new File("opmidcode.txt");
            } else {
                file = midcodeFile;
            }
            this.opWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<MidCodeEntry> getMidCodeList() {
        return midCodeList;
    }

    public static ArrayList<Str> getStrList() {
        return strList;
    }

    public static ArrayList<MidCodeEntry> getGlobal() {
        return global;
    }

    public void saveMidCode() {
        try {
            writer.write(globalStart);
            Iterator iterator = global.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                writer.write(midCodeEntry.toString() + "\n");
            }
            writer.write(globalEnd);
            writer.write(asciizStart);
            iterator = strList.iterator();
            while (iterator.hasNext()) {
                Str str = (Str) iterator.next();
                writer.write(str.toString() + "\n");
            }
            writer.write(asciizEnd);
            iterator = midCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                writer.write(midCodeEntry.toString() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOpMidCode() {
        try {
            opWriter.write(globalStart);
            Iterator iterator = global.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                opWriter.write(midCodeEntry.toString() + "\n");
            }
            opWriter.write(globalEnd);
            opWriter.write(asciizStart);
            iterator = strList.iterator();
            while (iterator.hasNext()) {
                Str str = (Str) iterator.next();
                opWriter.write(str.toString() + "\n");
            }
            opWriter.write(asciizEnd);
            iterator = opMidCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                opWriter.write(midCodeEntry.toString() + "\n");
            }
            opWriter.flush();
            opWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public static String getFuncName() {
        return funcName;
    }

    public static int getLayer() {
        return layer;
    }

    public static void addLayer() {
        layer++;
    }

    public static void subLayer() {
        layer--;
    }

    public static void addMidCodeEntry(MidCodeEntry midCodeEntry) {
        if (midCodeEntry.getOpType() == OpType.GLOBAL_DECLARE) {
            global.add(midCodeEntry);
        } else {
            midCodeList.add(midCodeEntry);
        }
    }

    public static void startFuncDef(String funcName) {
        MidCodeGener.funcName = funcName;
        MidCodeGener.getSymbolTable().reset();
        //生成中间代码时 重置sp
    }

    public static String genTemp() {
        String temp = "@T_" + Integer.toString(temp_num++);
        symbolTable.insertLocalTemp(temp,funcName);
        return temp;
    }

    public static String genStr(String content) {
        String str = "str_" + Integer.toString(str_num++);
        strList.add(new Str(content,str));
        return str;
    }

    public static String genLabel() {
        return String.format("label_%d",label_num++);
    }

    public static void setMidCodeList(ArrayList<MidCodeEntry> optimizedMidCode) {
        opMidCodeList = optimizedMidCode;
    }

    public static ArrayList<MidCodeEntry> getOpMidCodeList() {
        return opMidCodeList;
    }
}
