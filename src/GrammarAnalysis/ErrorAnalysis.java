package GrammarAnalysis;

import ASTNode.Node;
import Enum.ErrorType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ErrorAnalysis {
    private FileWriter writer;
    private ArrayList<Error> errorList = new ArrayList<>();
    private SymbolTable symbolTable = new SymbolTable();
    private Node root;

    public ErrorAnalysis(File errorFile) {
        try {
            this.writer = new FileWriter(errorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addError(int line, ErrorType type) {
        Error error;
        error = new Error(line,type);
        errorList.add(error);
    }

    public void deliverAST(Node node) {
        root = node;
    }

    public void checkError() {

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
