package ASTNode;

import Enum.ErrorType;
import GrammarAnalysis.ErrorAnalysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
public class FuncFParams extends Node {
    private ArrayList<FuncFParam> funcFParams = new ArrayList<>();

    public FuncFParams(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        funcFParams.add((FuncFParam) node);
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    public void checkError() {
        Set nameSet = new HashSet();
        for (FuncFParam funcFParam:funcFParams) {
            if (nameSet.contains(funcFParam.getName())) {
                ErrorAnalysis.addError(funcFParam.getLine(),ErrorType.reDef);
            } else {
                nameSet.add(funcFParam.getName());
            }
        }
    }

}
