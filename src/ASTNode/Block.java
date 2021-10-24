package ASTNode;
import GrammarAnalysis.ErrorAnalysis;
import WordAnalysis.Word;

import java.util.ArrayList;
public class Block extends Node {
    private ArrayList<Node> blockItems = new ArrayList<>();
    private Word lastRBRACE = null;

    public Block(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        blockItems.add(node);
    }

    public ArrayList<Node> getBlockItems() {
        return blockItems;
    }


    public void checkError() {
        ErrorAnalysis.addLayer();
        for (Node item:blockItems) {
            item.checkError();
        }
        ErrorAnalysis.getSymbolTable().removeLocal(ErrorAnalysis.getLayer(),ErrorAnalysis.getFuncName());
        ErrorAnalysis.subLayer();
    }

    public Word getLastRBRACE() {
        return lastRBRACE;
    }

    public void setLastRBRACE(Word lastRBRACE) {
        this.lastRBRACE = lastRBRACE;
    }

    public boolean lastReturned() {
        if (blockItems.size() == 0) {
            return false;
        }
        Node last = blockItems.get(blockItems.size() - 1);
        if (last instanceof ReturnStmt && !((ReturnStmt) last).isVoid()) {
            return true;
        } else {
            return false;
        }
    }
}
