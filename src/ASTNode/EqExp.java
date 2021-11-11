package ASTNode;

import Enum.CalType;

import java.util.ArrayList;
public class EqExp extends Node {
    private ArrayList<Node> RelExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

    public EqExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        RelExps.add(node);
    }

    public void checkError() {
        for (Node node:RelExps) {
            node.checkError();
        }
    }

    public void insertCaltype(String word) {
        assert word.equals("==") || word.equals("!=");
        if (word.equals("==")) {
            calTypes.add(CalType.eq);
        } else {
            calTypes.add(CalType.neq);
        }
    }
}
