import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ErrorAnalysis {
    private FileWriter writer;
    private ArrayList<Error> errorList = new ArrayList<>();
    private ArrayList<Node> loopStmts;
    private Node root;
    private int layer = 0;

    public ErrorAnalysis(File errorFile) {
        try {
            this.writer = new FileWriter(errorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addError(int line,ErrorType type) {
        Error error;
        error = new Error(line,type);
        errorList.add(error);
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

    public void deliverInfo(ArrayList<Node> loopStmts, Node node) {
        this.loopStmts = loopStmts;
        this.root = node;
    }

    public void scanAST() {
        checkLoopStmts();
        checkError(root);
    }

    public void checkError(Node node) {
        if (node.getType() == GrammarType.CompUnit) {
            for (Node child:node.getChildList()) {

            }
        } else if (node.getType() )

    }

    public void checkConstDecl(Node node) {
        assert node.getType() == GrammarType.ConstDecl;

    }

    public void checkVarDecl() {

    }

    public void checkLoopStmts() {
        for (Node node:loopStmts) {
            if (!inLoop(node)) {
                addError(node.getLine(),ErrorType.loopError);
            }
        }
    }

    public boolean inLoop(Node node) {
        Node father = node.getFather();
        while (father.getType() != GrammarType.FuncDef && father.getType() != GrammarType.FuncDef) {
            if (father.getType() == GrammarType.WhileStmt) {
                return true;
            }
        }
        return false;
    }

}
