package MidCodeGeneration;

import GrammarAnalysis.SymbolTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MidCodeGener {
    private FileWriter writer;
    private static ArrayList<MidCodeEntry> midCodeList = new ArrayList<>();
    private static ArrayList<Str> strList = new ArrayList<>();
    private static SymbolTable symbolTable = new SymbolTable();
    private static int layer = 0;

    private static String funcName = null;

    private static int temp_num = 0;
    private static int str_num = 0;

    public MidCodeGener(File midcodeFile) {
        try {
            this.writer = new FileWriter(midcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MidCodeEntry> getMidCodeList() {
        return midCodeList;
    }

    public void saveMidCode() {
        try {
            writer.write("########################################ASCIIZ START########################################\n");
            Iterator iterator = strList.iterator();
            while (iterator.hasNext()) {
                Str str = (Str) iterator.next();
                writer.write(str.toString() + "\n");
            }
            writer.write("########################################ASCIIZ END########################################\n");
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
        midCodeList.add(midCodeEntry);
    }

    public static void startFuncDef(String funcName) {
        MidCodeGener.funcName = funcName;
        MidCodeGener.getSymbolTable().reset();
        //重置sp
    }

    public static String genTemp() {
        String temp = "T_" + Integer.toString(temp_num++);
        symbolTable.insertLocalTemp(temp,funcName);
        return temp;
    }

    public static String genStr(String content) {
        String str = "str_" + Integer.toString(str_num++);
        strList.add(new Str(content,str));
        return str;
    }
}
