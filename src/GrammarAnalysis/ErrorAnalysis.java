package GrammarAnalysis;

import Enum.ErrorType;
import WordAnalysis.Word;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ErrorAnalysis {
    private FileWriter writer;
    private static ArrayList<Error> errorList = new ArrayList<>();
    private static SymbolTable symbolTable = new SymbolTable();
    private static int layer = 0;

    private static String funcName = null;

    private static boolean hasReturned = false;

    private static Word returned;

    public static void startFuncDef(String funcName) {
        ErrorAnalysis.funcName = funcName;
        hasReturned = false;
        returned = null;
    }

    public ErrorAnalysis(File errorFile) {
        try {
            this.writer = new FileWriter(errorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasReturned() {
        return hasReturned;
    }

    public static void setReturned(boolean hasReturned) {
        ErrorAnalysis.hasReturned = hasReturned;
    }

    public static Word getReturned() {
        return returned;
    }

    public static void setReturned(Word returned) {
        ErrorAnalysis.returned = returned;
    }

    public static void addError(int line, ErrorType type) {
        Error error;
        error = new Error(line,type);
        errorList.add(error);
    }

    public static SymbolTable getSymbolTable() {
        return symbolTable;
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

    public static String getFuncName() {
        return funcName;
    }

    public void saveErrorAnalysis() {
        Collections.sort(errorList);
        try {
            Iterator iterator = errorList.iterator();
            while (iterator.hasNext()) {
                Error error = (Error) iterator.next();
                writer.write(String.format("%d %c\n",error.getLine(),error.getErrorCode()));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
