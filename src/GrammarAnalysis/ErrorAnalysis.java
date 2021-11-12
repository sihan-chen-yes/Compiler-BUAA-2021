package GrammarAnalysis;

import Enum.DataType;
import Enum.ErrorType;

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
    private static boolean on = true;
    //是否将错误加入错误列表

    private static String funcName = null;
    private static DataType retType;

    public static void startFuncDef(String funcName,DataType dataType) {
        ErrorAnalysis.funcName = funcName;
        ErrorAnalysis.retType = dataType;
    }

    public static DataType getRetType() {
        return retType;
    }

    public ErrorAnalysis(File errorFile) {
        try {
            this.writer = new FileWriter(errorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addError(int line, ErrorType type) {
        if (on) {
            Error error;
            error = new Error(line,type);
            errorList.add(error);
        }
    }

    public static void setOn(boolean on) {
        ErrorAnalysis.on = on;
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
                writer.write(error.getMsg() + " " + String.format("%d %c\n",error.getLine(),error.getErrorCode()));
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean findErrors() {
        return !errorList.isEmpty();
    }
}
