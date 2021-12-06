package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import Optimizer.Optimizer;

public class WhileStmt extends Node {
    private String label1 = null;
    private Cond Cond;
    private String label2 = null;
    private Node body;
    private String label3 = null;

    public WhileStmt(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        if (node instanceof Cond) {
            Cond = (Cond) node;
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
        if (!Optimizer.isOp()) {
            label1 = MidCodeGener.genLabel();
            if (Cond.getLAndExpNum() > 1) {
                //如果只有一个And块不用产生label2
                label2 = MidCodeGener.genLabel();
            }
            label3 = MidCodeGener.genLabel();
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            label1
                    )
            );
            Cond.genMidCode();
            if (Cond.getLAndExpNum() > 1) {
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
                                OpType.LABEL_GEN,
                                null,null,null,
                                label2
                        )
                );
            }
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
        } else {
            //优化 删去GOTO 和label1
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
            Cond.genMidCode();
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
