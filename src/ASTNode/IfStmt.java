package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

public class IfStmt extends Node {
    private Cond Cond;
    private String label1 = null;
    private Node IfStmt = null;
    private String label2 = null;
    private Node ElseStmt = null;
    private String label3 = null;

    public IfStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = (Cond) node;
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

    @Override
    public String genMidCode() {
        if (Cond.getLAndExpNum() > 1) {
            label1 = MidCodeGener.genLabel();
        }
        label2 = MidCodeGener.genLabel();
        Cond.genMidCode();
        if (Cond.getLAndExpNum() > 1) {
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            label1
                    )
            );
        }
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
