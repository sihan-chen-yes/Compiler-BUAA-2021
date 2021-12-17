package Optimizer;

import Enum.OpType;
import GrammarAnalysis.SymbolTable;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.ArrayList;
import java.util.HashSet;

public class FuncBlock {
    private HeadBlock headBlock;
    private ArrayList<BasicBlock> basicBlocks = new ArrayList<>();
    private EndBlock endBlock = new EndBlock();
    private String func;
    private ArrayList<MidCodeEntry> opMidCodeList = new ArrayList<>();

    private ConflictGraph conflictGraph = new ConflictGraph();

    public FuncBlock(MidCodeEntry midCodeEntry) {
        headBlock = new HeadBlock(midCodeEntry);
        midCodeEntry.setFuncBlock(this);
        func = midCodeEntry.getDst();
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        basicBlock.setFunc(func);
        basicBlocks.add(basicBlock);
    }

    public ArrayList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public HeadBlock getHeadBlock() {
        return headBlock;
    }

    public EndBlock getEndBlock() {
        return endBlock;
    }


    public String getFunc() {
        return func;
    }

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        ArrayList<MidCodeEntry> optimizedMidCode = new ArrayList<>();
        optimizedMidCode.add(headBlock.getMidCodeEntry());
        for(BasicBlock basicBlock:basicBlocks) {
            optimizedMidCode.addAll(basicBlock.getOptimizedMidCode());
        }
        return optimizedMidCode;
    }

    public ArrayList<MidCodeEntry> getMidCode() {
        ArrayList<MidCodeEntry> midCode = new ArrayList<>();
        midCode.add(headBlock.getMidCodeEntry());
        for(BasicBlock basicBlock:basicBlocks) {
            midCode.addAll(basicBlock.getMidCode());
        }
        return midCode;
    }

    public void genReachDef() {
        for (BasicBlock basicBlock:basicBlocks) {
            for (MidCodeEntry midCodeEntry:basicBlock.getMidCodeList()) {
                //更新每个midcode的kill
                if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                    midCodeEntry.genGenKill(this);
                }
            }
        }
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genGenKillSet();
        }
    }

    public void genDefUse() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genUseDefSet();
        }
        while (true) {
            boolean stop = true;
            for (int i = basicBlocks.size() - 1;i >= 0;i--) {
                if (basicBlocks.get(i).genUseDefOutSet()) {
                    stop = false;
                }
                basicBlocks.get(i).genUseDefInSet();
            }
            if (stop) {
                break;
            }
        }
        for (int i = basicBlocks.size() - 1;i >= 0;i--) {
            basicBlocks.get(i).genUseDef();
        }
    }

    public void dye() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.genSubConf(conflictGraph);
        }
        conflictGraph.dye();
    }

    public ConflictGraph getConflictGraph() {
        return conflictGraph;
    }

    public void delDeadCode() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.delDeadCode();
        }
    }

    public void spread() {
        for (BasicBlock basicBlock:basicBlocks) {
            basicBlock.spread();
        }
    }

    public void delWhile(ArrayList<FuncBlock> funcBlocks) {
        ArrayList<BasicBlock> tmp = new ArrayList<>();
        for (BasicBlock basicBlock:basicBlocks) {
            if (!deletable(basicBlock,funcBlocks)) {
                tmp.add(basicBlock);
            } else {
                tmp.remove(tmp.get(tmp.size() - 1));
            }
        }
        basicBlocks = tmp;
    }

    public boolean selfWhile(BasicBlock basicBlock) {
        ArrayList<BasicBlock> postBlocks = basicBlock.getPostBlocks();
        ArrayList<BasicBlock> preBlocks = basicBlock.getPreBlocks();
        if (postBlocks.contains(basicBlock) && preBlocks.contains(basicBlock)) {
            return true;
        }
        return false;
    }

    public boolean deletable(BasicBlock basicBlock,ArrayList<FuncBlock> funcBlocks) {
        ArrayList<MidCodeEntry> midCodeList = basicBlock.getMidCodeList();
        if (!(basicBlock.getPreBlocks().size() == 2 && basicBlock.getPostBlocks().size() == 2 && selfWhile(basicBlock))) {
            return false;
        }
        HashSet<String> assignDef= new HashSet<>();
        ArrayList<MidCodeEntry> blockMid = basicBlock.getMidCode();
        for (MidCodeEntry midCodeEntry:blockMid) {
            if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                assignDef.add(midCodeEntry.getR1());
            }
        }
        BasicBlock nextBlock = null;
        for (BasicBlock next:basicBlock.getPostBlocks()) {
            if (next.getBlockNum() != basicBlock.getBlockNum()) {
                nextBlock = next;
                break;
            }
        }
        HashSet<String> nextIn = nextBlock.getUseDefInSet();
        for (String var:assignDef) {
            if (nextIn.contains(var)) {
                return false;
            }
        }
        //检查局部变量
        for (MidCodeEntry midCodeEntry:midCodeList) {
            OpType opType = midCodeEntry.getOpType();
            if (opType == OpType.PUSH_PARAM || opType == OpType.STORE_ARRAY_1D || opType == opType.STORE_ARRAY_2D
            || opType == OpType.PRINT_INT || opType == OpType.PRINT_STRING || opType == OpType.RET_VALUE || opType == OpType.RET_VOID
            || opType == OpType.GETINT || opType == OpType.PREPARE_CALL || opType == OpType.CALL || opType == OpType.FIN_CALL) {
                return false;
            } else if (opType == OpType.ASSIGN) {
                String var = midCodeEntry.getR1();
                SymbolTable symbolTable = MidCodeGener.getSymbolTable();
                MidCodeEntry last = basicBlock.getLastMidCodeEntry();
                if (symbolTable.search_local(func,var) == null && symbolTable.search_global(var) != null) {
                    //是全局变量 更改了全局变量
                    ArrayList<MidCodeEntry> midCodes = getMidCode();
                    //当前函数的中间代码
                    boolean start = false;
                    for (MidCodeEntry midCode:midCodes) {
                        if (start) {
                            //后面的中间代码有用到此全局变量
                            if (midCode.getUseGlobal().contains(var)) {
                                return false;
                            }
                            if (midCode.getOpType() == OpType.CALL) {
                                String calledFunc = midCode.getDst();
                                FuncBlock called = null;
                                for (FuncBlock funcBlock:funcBlocks) {
                                    if (funcBlock.getFunc().equals(calledFunc)) {
                                        called = funcBlock;
                                        break;
                                    }
                                }
                                //调用的函数有用到此全局变量
                                if (checkFuncUse(called,var)) {
                                    return false;
                                }
                            }
                        }
                        if (midCode.equals(last)) {
                            start = true;
                            //从此处开始检查
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean checkFuncUse(FuncBlock funcBlock,String var) {
        //是否使用过该全局变量
        SymbolTable symbolTable = MidCodeGener.getSymbolTable();
        assert symbolTable.search_local(funcBlock.getFunc(),var) == null
                && symbolTable.search_global(var) != null;
        ArrayList<MidCodeEntry> midCodeList = funcBlock.getMidCode();
        for (MidCodeEntry midCodeEntry:midCodeList) {
            if (midCodeEntry.getUseGlobal().contains(var)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String results = headBlock.toString() + "\n";
        for (int i = 0;i < basicBlocks.size();i++) {
            results += basicBlocks.get(i).toString() + "\n";
        }
        return results;
    }
}
