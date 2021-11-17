package ASTNode;
import Enum.*;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;

public class LAndExp extends Node {
    private ArrayList<Node> EqExps =new ArrayList<>();

    public LAndExp(int pos) {
        super(pos);
    }

    @Override
    public void link(Node node) {
        super.link(node);
        EqExps.add(node);
    }

    public void checkError() {
        for (Node eqexp:EqExps) {
            eqexp.checkError();
        }
    }

    public String genMidCode(boolean isLast,String label) {
        if (isLast) {
            //是And块中的最后一块
            for (int i = 0;i < EqExps.size();i++) {
                String temp = EqExps.get(i).genMidCode();
                //有一个!Cond直接结束
                MidCodeGener.addMidCodeEntry(
                        new MidCodeEntry(
                            OpType.BEQZ,
                                temp,
                                null,
                                null,
                                label
                        )
                );
            }
        } else {
            //不是And块中的最后一块
            String andBlockLabel = MidCodeGener.genLabel();
            for (int i = 0;i < EqExps.size();i++) {
                String temp = EqExps.get(i).genMidCode();
                if (i == EqExps.size() - 1) {
                    //是最后一个C
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.BNEZ,
                                    temp,
                                    null,
                                    null,
                                    label
                            )
                    );
                } else {
                    //不是最后一个C
                    MidCodeGener.addMidCodeEntry(
                            new MidCodeEntry(
                                    OpType.BEQZ,
                                    temp,
                                    null,
                                    null,
                                    andBlockLabel
                            )
                    );
                }
            }
            //结束以后留下label
            MidCodeGener.addMidCodeEntry(
                    new MidCodeEntry(
                            OpType.LABEL_GEN,
                            null,null,null,
                            andBlockLabel
                    )
            );
        }
        return super.genMidCode();
    }
}
