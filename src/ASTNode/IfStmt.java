package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class IfStmt extends Node {
    private Node Cond;
    private String label1;
    private Node IfStmt = null;
    private String label2;
    private Node ElseStmt = null;
    private String label3;

    public IfStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = node;
        } else if (IfStmt == null) {
            IfStmt = node;
        } else {
            ElseStmt = node;
        }
    }

    public void checkError() {
        Cond.checkError();
        if (IfStmt != null) {
            IfStmt.checkError();
        }
        if (ElseStmt != null) {
            ElseStmt.checkError();
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
        Cond.genMidCode();
        MidCodeGener.addMidCodeEntry(
                new MidCodeEntry(
                        OpType.LABEL_GEN,
                        null,null,null,
                        label1
                )
        );
        IfStmt.genMidCode();
        if (ElseStmt == null) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            label2
                    )
            );
        }  else {
            label3 = MidCodeGener.genLabel();
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.GOTO,
                            null,null,null,
                            label3
                            )
            );
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            label2
                    )
            );
            ElseStmt.genMidCode();
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            label3
                    )
            );
        }
        return super.genMidCode();
    }
}
