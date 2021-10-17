import java.lang.reflect.Type;
import java.util.ArrayList;

public class Node {
    private Word word = null;
    //非终结符没有word
    private GrammarType type;
    private Node father = null;
    private ArrayList<Node> childList = new ArrayList<>();
    int pos;

    public Node(Word word,int pos) {
        //如果是非终结符
        this.word = word;
        type = GrammarType.Terminal;
        this.pos = pos;
    }

    public Node(GrammarType type,int pos) {
        this.type = type;
        this.pos = pos;
    }

    public void link(Node node) {
        childList.add(node);
        node.setFather(this);
    }

    public void setFather(Node node) {
        father = node;
    }

    public Node getFather() {
        return father;
    }
}
