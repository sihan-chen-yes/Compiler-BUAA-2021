package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class WhileStmt extends Node {
    private String label1;
    private Node Cond;
    private String label2;
    private Node body;
    private String label3;

    public WhileStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = node;
        } else {
            body = node;
        }
    }

    public void checkError() {
        if (Cond != null) {
            Cond.checkError();
        }
        if (body != null) {
            body.checkError();
        }
    }

    public String getLabel1() {
        return label1;
    }

    public String getLabel2() {
        return label2;
    }

    public String getLabel3() {
        return label3;
    }

    @Override
    public String genMidCode() {
        label1 = MidCodeGener.genLabel();
        label2 = MidCodeGener.genLabel();
        label3 = MidCodeGener.genLabel();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.LABEL_GEN,
                        null,null,null,
                        label1
                )
        );
        Cond.genMidCode();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.LABEL_GEN,
                        null,null,null,
                        label2
                )
        );
        body.genMidCode();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.GOTO,
                        null,null,null,
                        label1
                )
        );
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.LABEL_GEN,
                        null,null,null,
                        label3
                )
        );
        return super.genMidCode();
    }
}
