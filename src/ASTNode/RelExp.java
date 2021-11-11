package ASTNode;

import Enum.CalType;

import java.util.ArrayList;
public class RelExp extends Node {
    private ArrayList<Node> AddExps = new ArrayList<>();
    private ArrayList<CalType> calTypes = new ArrayList<>();

    public RelExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        AddExps.add(node);
    }

    public void checkError() {
        for (Node addexp:AddExps) {
            addexp.checkError();
        }
    }

    public void insertCaltype(String word) {
        assert word.equals("<") || word.equals("<=") || word.equals(">") || word.equals(">=");
        if (word.equals("<")) {
            calTypes.add(CalType.lt);
        } else if (word.equals("<=")) {
            calTypes.add(CalType.le);
        } else if (word.equals(">")) {
            calTypes.add(CalType.gt);
        } else {
            calTypes.add(CalType.ge);
        }
    }
}
