package Optimizer;

import MidCodeGeneration.MidCodeEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BasicBlock {
    private ArrayList<BasicBlock> preBlocks = new ArrayList<>();
    private ArrayList<MidCodeEntry> midCodeEntries = new ArrayList<>();
//    private ArrayList<MidCodeEntry> optimizedMidCode = new ArrayList<>();
    private ArrayList<BasicBlock> postBlocks = new ArrayList<>();
    private String func;
    private int blockNum;
    private ArrayList<String> labels = new ArrayList<>();

    private ArrayList<String> deletedOperand = new ArrayList<>();
    private HashMap<Integer,DAGNode> dagNodes = new HashMap<>();
    private HashMap<String,Integer> record = new HashMap<>();


    private HashSet<defPoint> genSet = new HashSet<>();
    private HashSet<defPoint> killSet = new HashSet<>();
    private HashSet<defPoint> reachDefInSet = new HashSet<>();
    private HashSet<defPoint> reachDefOutSet = new HashSet<>();
    private ArrayList<defPoint> defPoints = new ArrayList<>();

    private HashSet<String> useSet = new HashSet<>();
    private HashSet<String> defSet = new HashSet<>();
    private HashSet<String> defUseInSet = new HashSet<>();
    private HashSet<String> defUseOutSet = new HashSet<>();

    public void addMideCodeEntry(MidCodeEntry midCodeEntry) {
        midCodeEntries.add(midCodeEntry);
    }

    public void link(BasicBlock basicBlock) {
        this.addPostBlock(basicBlock);
        basicBlock.addPreBlock(this);
    }

    public void addPreBlock(BasicBlock pre) {
        preBlocks.add(pre);
    }

    public void addPostBlock(BasicBlock post) {
        postBlocks.add(post);
    }

    public ArrayList<BasicBlock> getPreBlocks() {
        return preBlocks;
    }

    public ArrayList<BasicBlock> getPostBlocks() {
        return postBlocks;
    }

    public ArrayList<MidCodeEntry> getMidCodeList() {
        return midCodeEntries;
    }

    public void genGenSet() {

        
    }

    public void genKillSet() {

    }

    public void genUseSet() {

    }

    public void genDefSet() {

    }



//    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
//        return optimizedMidCode;
//    }
//
//    public void addOptimizedMidCode(MidCodeEntry midCodeEntry) {
//        optimizedMidCode.add(midCodeEntry);
//    }

    public MidCodeEntry getLastMidCodeEntry() {
        assert midCodeEntries.size() > 0;
        return midCodeEntries.get(midCodeEntries.size() - 1);
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public int getBlockNum() {
        return blockNum;
    }

//    public void genDAG() {
//        //尽量用T 一定是最新值
//        //操作数可能是 @T_ a 1
//        for (MidCodeEntry midCodeEntry:midCodeEntries) {
//            if (midCodeEntry.getOpType() == OpType.PUSH_PARAM) {
//                if (needChange(midCodeEntry.getR1())) {
//                    midCodeEntry.setR1(replace(midCodeEntry.getR1()));
//                }
//            } else if (midCodeEntry.getOpType() == OpType.LOAD_ARRAY_1D) {
//                if (find(midCodeEntry)) {
//                    deletedOperand.add(midCodeEntry.getDst());
//                    continue;
//                }
//            } else if (midCodeEntry.getOpType() == OpType.LOAD_ARRAY_2D) {
//                if (find(midCodeEntry)) {
//                    deletedOperand.add(midCodeEntry.getDst());
//                    continue;
//                }
//            } else if (midCodeEntry.getOpType() == OpType.STORE_ARRAY_1D) {
//                find(midCodeEntry);
//            } else if (midCodeEntry.getOpType() == OpType.STORE_ARRAY_2D) {
//                find(midCodeEntry);
//            } else if (midCodeEntry.getOpType() == OpType.LOAD_ARRDESS) {
//                if (find(midCodeEntry)) {
//                    deletedOperand.add(midCodeEntry.getDst());
//                    continue;
//                }
//            } else if (midCodeEntry.getOpType() == OpType.ASSIGN) {
//                assert isVar(midCodeEntry.getR1());
//                DAGNode dagNode;
//                if (hasOperandNode(midCodeEntry.getDst())) {
//                    dagNode = getNode(midCodeEntry.getDst());
//                } else {
//                    dagNode = genOperandNode(midCodeEntry.getDst());
//                }
//                assert hasOperandNode(midCodeEntry.getDst());
//                dagNode.addAlias(midCodeEntry.getR1());
//                record.put(midCodeEntry.getR1(),dagNode.getNodeNum());
//                continue;
//            } else if (midCodeEntry.getOpType() == OpType.PRINT_INT) {
//                if (needChange(midCodeEntry.getDst())) {
//                    midCodeEntry.setDst(replace(midCodeEntry.getDst()));
//                }
//            } else if (midCodeEntry.getOpType() == OpType.CALL) {
//                //写回 Todo 跨函数分析
//                recordLocal();
//                reset();
//            } else if (isExp(midCodeEntry)) {
//                if (find(midCodeEntry)) {
//                    deletedOperand.add(midCodeEntry.getDst());
//                    continue;
//                }
//            } else if (midCodeEntry.getOpType() == OpType.BEQZ) {
//                if (needChange(midCodeEntry.getR1())) {
//                    midCodeEntry.setR1(replace(midCodeEntry.getR1()));
//                }
//                recordLocal();
//                optimizedMidCode.add(midCodeEntry);
//                return;
//            } else if (midCodeEntry.getOpType() == OpType.BNEZ) {
//                if (needChange(midCodeEntry.getR1())) {
//                    midCodeEntry.setR1(replace(midCodeEntry.getR1()));
//                }
//                recordLocal();
//                optimizedMidCode.add(midCodeEntry);
//                return;
//            } else if (midCodeEntry.getOpType() == OpType.RET_VALUE) {
//                if (needChange(midCodeEntry.getDst())) {
//                    midCodeEntry.setDst(replace(midCodeEntry.getDst()));
//                }
//                //需要在ret之前写回
//                recordLocal();
//                optimizedMidCode.add(midCodeEntry);
//                return;
//            } else if (midCodeEntry.getOpType() == OpType.RET_VOID) {
//                //需要在ret之前写回
//                recordLocal();
//                optimizedMidCode.add(midCodeEntry);
//                return;
//            }
//            optimizedMidCode.add(midCodeEntry);
//        }
//    }
//
//    public void recordLocal() {
//        Iterator iterator = record.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String,Integer> entry = (Map.Entry<String,Integer>) iterator.next();
//            String name = entry.getKey();
//            if (isVar(name) && !MidCodeGener.getSymbolTable().isArray(func,name)) {
//                MidCodeEntry midCodeEntry = new MidCodeEntry(OpType.ASSIGN,name,null,null,replace(name));
//                optimizedMidCode.add(midCodeEntry);
//            }
//        }
//    }

//    public void reset() {
//        deletedOperand = new ArrayList<>();
//        dagNodes = new HashMap<>();
//        record = new HashMap<>();
//    }
//
//    public boolean isExp(MidCodeEntry midCodeEntry) {
//        OpType opType = midCodeEntry.getOpType();
//        if (opType == OpType.ADD || opType == OpType.SUB || opType == OpType.MULT || opType == OpType.DIV
//                || opType == OpType.MOD || opType == OpType.NEG ||opType == OpType.SLT || opType == OpType.SLE || opType == OpType.SGT
//                || opType == OpType.SGE || opType == OpType.SEQ || opType == OpType.SNE || opType == OpType.NOT) {
//            return true;
//        } else {
//            return false;
//        }
//    }

//    public boolean needChange(String name) {
//        if (isTemp(name) && deletedOperand.contains(name) || isVar(name)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public boolean find(MidCodeEntry midCodeEntry) {
//        DAGNode left;
//        DAGNode right;
//        DAGNode middle;
//        if (hasOperandNode(midCodeEntry.getR1())) {
//            left = getNode(midCodeEntry.getR1());
//            midCodeEntry.setR1(replace(midCodeEntry.getR1()));
//        } else {
//            left = genOperandNode(midCodeEntry.getR1());
//        }
//        if (midCodeEntry.getR2() == null) {
//            right = null;
//        } else {
//            if (hasOperandNode(midCodeEntry.getR2())) {
//                right = getNode(midCodeEntry.getR2());
//                midCodeEntry.setR2(replace(midCodeEntry.getR2()));
//            } else {
//                right = genOperandNode(midCodeEntry.getR2());
//            }
//        }
//        if (midCodeEntry.getR3() == null) {
//            middle = null;
//        } else {
//            if (hasOperandNode(midCodeEntry.getR3())) {
//                middle = getNode(midCodeEntry.getR3());
//                midCodeEntry.setR3(replace(midCodeEntry.getR3()));
//            } else {
//                middle = genOperandNode(midCodeEntry.getR3());
//            }
//        }
//        DAGNode father = searchCalNode(midCodeEntry.getOpType(),left,middle,right);
//        if (father != null) {
//            record.put(midCodeEntry.getDst(), father.getNodeNum());
//            father.addAlias(midCodeEntry.getDst());
//            return true;
//        } else {
//            father = genCalNode(midCodeEntry.getOpType(),left,middle,right);
//            record.put(midCodeEntry.getDst(), father.getNodeNum());
//            father.addAlias(midCodeEntry.getDst());
//            return false;
//        }
//    }
//
//    public boolean hasOperandNode(String name) {
//        if (record.containsKey(name)) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public DAGNode searchCalNode(OpType opType,DAGNode left,DAGNode middle,DAGNode right) {
//        Iterator iterator = dagNodes.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<Integer,DAGNode> entry = (Map.Entry<Integer,DAGNode>)iterator.next();
//            DAGNode dagNode = entry.getValue();
//            if (dagNode.getOpType() == opType && dagNode.getLeftNodeNum() == left.getNodeNum()
//                    && (right != null && dagNode.getRightNodeNum() == right.getNodeNum() || right == null)
//                    && (middle != null && dagNode.getMiddleNodeNum() == middle.getNodeNum() || middle == null)) {
//                return dagNode;
//            }
//        }
//        return null;
//    }
//
//    public DAGNode genCalNode(OpType opType,DAGNode left,DAGNode middle,DAGNode right) {
//        DAGNode node = new DAGNode(opType, blockNum);
//        node.setLeftNode(left);
//        if (right != null) {
//            node.setRightNode(right);
//        }
//        if (middle != null) {
//            node.setMiddleNode(middle);
//        }
//        dagNodes.put(blockNum,node);
//        blockNum++;
//        return node;
//    }
//
//    public boolean isTemp(String name) {
//        Pattern pattern = Pattern.compile("^@T_\\d*");
//        Matcher matcher = pattern.matcher(name);
//        if (matcher.matches()) {
//            return true;
//        }
//        return false;
//    }
//
//    public boolean isConst(String name) {
//        Pattern pattern = Pattern.compile("\\d*");
//        Matcher matcher = pattern.matcher(name);
//        if (matcher.matches()) {
//            return true;
//        }
//        return false;
//    }
//
//    public boolean isVar(String name) {
//        return !isConst(name) && !isTemp(name);
//    }
//
//    public DAGNode genOperandNode(String name) {
//        DAGNode node = new DAGNode(name, blockNum);
//        record.put(name, blockNum);
//        dagNodes.put(blockNum,node);
//        blockNum++;
//        return node;
//    }
//
//    public DAGNode getNode(String name) {
//        DAGNode dagNode = dagNodes.get(record.get(name));
//        return dagNode;
//    }
//
//    public String replace(String name) {
//        DAGNode dagNode = getNode(name);
//        if (dagNode == null) {
//            return name;
//        } else if (dagNode.getChange(deletedOperand) == null) {
//            return name;
//        } else {
//            return dagNode.getChange(deletedOperand);
//        }
//    }

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getFunc() {
        return func;
    }



















    @Override
    public String toString() {
        String results = String.format("#~~~~~~~~~~~~~~~~Block%d~~~~~~~~~~~~~~~~\n", blockNum);
        for (String label:labels) {
            results += String.format("LABEL_GEN %s\n",label);
        }
        for (int i = 0;i < midCodeEntries.size();i++) {
            results += midCodeEntries.get(i).toString() + "\n";
        }
        return results;
    }
}
