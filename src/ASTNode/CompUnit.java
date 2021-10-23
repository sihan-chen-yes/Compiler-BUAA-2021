package ASTNode;

import java.util.ArrayList;

public class CompUnit extends Node {
    private ArrayList<Node> Decls = new ArrayList<>();
    private ArrayList<Node> FuncDefs = new ArrayList<>();
    private Node MainFuncDef = null;

    public CompUnit(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof ConstDecl || node instanceof VarDecl) {
            Decls.add(node);
        } else {
            assert node instanceof MainFuncDef;
            MainFuncDef = node;
        }
    }

    public ArrayList<Node> getDecls() {
        return Decls;
    }

    public ArrayList<Node> getFuncDefs() {
        return FuncDefs;
    }

    public Node getMainFuncDef() {
        return MainFuncDef;
    }
}
