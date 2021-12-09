package Optimizer;

import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicBlock {
    private ArrayList<BasicBlock> preBlocks = new ArrayList<>();
    private ArrayList<MidCodeEntry> midCodeList = new ArrayList<>();
    private ArrayList<MidCodeEntry> assignMid = new ArrayList<>();
//    private ArrayList<MidCodeEntry> optimizedMidCode = new ArrayList<>();
    private ArrayList<BasicBlock> postBlocks = new ArrayList<>();
    private String func;
    private int blockNum;
    private ArrayList<String> labels = new ArrayList<>();

    private ArrayList<String> deletedOperand = new ArrayList<>();
    private HashMap<Integer,DAGNode> dagNodes = new HashMap<>();
    private HashMap<String,Integer> record = new HashMap<>();


    private HashSet<DefPoint> genSet = new HashSet<>();
    private HashSet<DefPoint> killSet = new HashSet<>();
    private HashSet<DefPoint> reachDefInSet = new HashSet<>();
    private HashSet<DefPoint> reachDefOutSet = new HashSet<>();
    private ArrayList<DefPoint> DefPoints = new ArrayList<>();

    private HashSet<String> useSet = new HashSet<>();
    private HashSet<String> defSet = new HashSet<>();
    private HashSet<String> useDefInSet = new HashSet<>();
    private HashSet<String> useDefOutSet = new HashSet<>();

    private ArrayList<String> sRegs = new ArrayList<>();
    private ArrayList<String> tRegs = new ArrayList<>();
    private HashMap<String, String> varToReg = new HashMap<>();

    public void addMideCodeEntry(MidCodeEntry midCodeEntry) {
        midCodeList.add(midCodeEntry);
        midCodeEntry.setFatherBlock(this);
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
        return midCodeList;
    }

    public void genGenKillSet() {
        for (int i = midCodeList.size() - 1;i >= 0;i--) {
            MidCodeEntry midCodeEntry = midCodeList.get(i);
            if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                assignMid.add(midCodeEntry);
            }
        }
        for (int i = assignMid.size() - 1;i >= 0;i--) {
            if (i == assignMid.size() - 1) {
                genSet.add(assignMid.get(i).getGen());
                killSet.addAll(assignMid.get(i).getKill());
            } else {
                HashSet<DefPoint> gen = new HashSet<>();
                gen.add(assignMid.get(i).getGen());
                gen.removeAll(killSet);
                genSet.addAll(gen);
                killSet.addAll(assignMid.get(i).getKill());
            }
        }
    }

    public HashSet<DefPoint> getGenSet() {
        return genSet;
    }

    public HashSet<DefPoint> getKillSet() {
        return killSet;
    }

    public boolean genUseDefOutSet() {
        int originNum = useDefOutSet.size();
        for (BasicBlock postBlock:postBlocks) {
            useDefOutSet.addAll(postBlock.getUseDefInSet());
        }
        return useDefOutSet.size() != originNum;
    }

    public void genUseDefInSet() {
        useDefInSet = new HashSet(useDefOutSet);
        useDefInSet.removeAll(defSet);
        useDefInSet.addAll(useSet);
        return;
    }

    public HashSet<String> getUseDefInSet() {
        return useDefInSet;
    }

    public HashSet<String> getUseDefOutSet() {
        return useDefOutSet;
    }

    public void genUseDefSet() {
        for (MidCodeEntry midCodeEntry:midCodeList) {
            if (midCodeEntry.getOpType() == OpType.LOAD_ARRDESS && needInUseSet(midCodeEntry.getR2())) {
                useSet.add(midCodeEntry.getR2());
            } else if (midCodeEntry.getOpType() == OpType.PUSH_PARAM
                    || midCodeEntry.getOpType() == OpType.NEG || midCodeEntry.getOpType() == OpType.NOT
                    || midCodeEntry.getOpType() == OpType.BEQZ || midCodeEntry.getOpType() == OpType.BNEZ) {
                if (needInUseSet(midCodeEntry.getR1())) {
                    useSet.add(midCodeEntry.getR1());
                }
            } else if ((midCodeEntry.getOpType() == OpType.LOAD_ARRAY_1D
                    || midCodeEntry.getOpType() == OpType.STORE_ARRAY_1D) && needInUseSet(midCodeEntry.getR2())) {
                useSet.add(midCodeEntry.getR2());
            } else if (midCodeEntry.getOpType() == OpType.LOAD_ARRAY_2D
                    || midCodeEntry.getOpType() == OpType.STORE_ARRAY_2D) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInUseSet(midCodeEntry.getR3())) {
                    useSet.add(midCodeEntry.getR3());
                }
            } else if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
                if (needInDefSet(midCodeEntry.getR1())) {
                    defSet.add(midCodeEntry.getR1());
                }
            } else if (midCodeEntry.getOpType() == OpType.ADD || midCodeEntry.getOpType() == OpType.SUB
                    || midCodeEntry.getOpType() == OpType.MULT || midCodeEntry.getOpType() == OpType.DIV
                    || midCodeEntry.getOpType() == OpType.MOD
                    || midCodeEntry.getOpType() == OpType.SLT || midCodeEntry.getOpType() == OpType.SLE
                    || midCodeEntry.getOpType() == OpType.SGT || midCodeEntry.getOpType() == OpType.SGE
                    || midCodeEntry.getOpType() == OpType.SEQ || midCodeEntry.getOpType() == OpType.SNE) {
                if (needInUseSet(midCodeEntry.getR1())) {
                    useSet.add(midCodeEntry.getR1());
                }
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
            } else if (midCodeEntry.getOpType() == OpType.PRINT_INT || midCodeEntry.getOpType() == OpType.RET_VALUE) {
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
            }
        }
    }

    public boolean needInUseSet(String name) {
        return name != null && isVar(name) && !defSet.contains(name);
    }

    public boolean needInDefSet(String name) {
        return name != null && isVar(name) && !useSet.contains(name);
    }

    public boolean isNumber(String name) {
        if (name != null) {
            Pattern pattern = Pattern.compile("^(-)?\\d+");
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTemp(String name) {
        if (name != null) {
            Pattern pattern = Pattern.compile("^@T_\\d+");
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    public boolean isVar(String name) {
        return name != null && !isNumber(name) && !isTemp(name);
    }

//    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
//        return optimizedMidCode;
//    }
//
//    public void addOptimizedMidCode(MidCodeEntry midCodeEntry) {
//        optimizedMidCode.add(midCodeEntry);
//    }

    public MidCodeEntry getLastMidCodeEntry() {
        assert midCodeList.size() > 0;
        return midCodeList.get(midCodeList.size() - 1);
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

    public void resetSRegs() {
        for (int i = 0; i < 8;i++) {
            sRegs.add(String.format("$s%d",i));
        }
    }

    public void resetTRegs() {
        for (int i = 0; i < 10;i++) {
            tRegs.add(String.format("$t%d",i));
        }
    }

    public ArrayList<String> getSRegs() {
        return sRegs;
    }

    public HashMap<String, String> getVarToReg() {
        return varToReg;
    }

    public void setVarToReg(HashMap<String, String> varToReg) {
        this.varToReg = new HashMap<>(varToReg);
    }

    public void setSRegs(ArrayList<String> sRegs) {
        this.sRegs = new ArrayList<>(sRegs);
    }

    public void setAllocation(BasicBlock basicBlock) {
        //传进来的基本块是前驱
        setSRegs(basicBlock.getSRegs());
        setVarToReg(basicBlock.getVarToReg());
        Iterator<Map.Entry<String, String>> iterator = varToReg.entrySet().iterator();
        HashSet<String> lastUseDefOutSet = basicBlock.getUseDefOutSet();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String var = entry.getKey();
            if (!lastUseDefOutSet.contains(var)) {
                //需要在外面remove一下
                releaseSReg(var);
                iterator.remove();
            }
        }
    }

    public void allocSReg(String var) {
        String sReg = sRegs.get(0);
        sRegs.remove(0);
        varToReg.put(var,sReg);
    }

    public void releaseSReg(String var) {
        sRegs.add(varToReg.get(var));
    }

    public boolean hasSReg(String var) {
        return var != null && varToReg.containsKey(var);
    }

    public String querySReg(String var) {
        return varToReg.get(var);
    }

    public void allocSRegs() {
        for (MidCodeEntry midCodeEntry:midCodeList) {
            if (sRegs.size() > 0 && !hasSReg(midCodeEntry.getR1()) && r1IsVar(midCodeEntry)) {
                allocSReg(midCodeEntry.getR1());
            }
            if (sRegs.size() > 0 && !hasSReg(midCodeEntry.getR2()) && r2IsVar(midCodeEntry)) {
                allocSReg(midCodeEntry.getR2());
            }
            if (sRegs.size() > 0 && !hasSReg(midCodeEntry.getR3()) && r3IsVar(midCodeEntry)) {
                allocSReg(midCodeEntry.getR3());
            }
            if (sRegs.size() > 0 && !hasSReg(midCodeEntry.getDst()) && dstIsVar(midCodeEntry)) {
                allocSReg(midCodeEntry.getDst());
            }
        }
    }

    public boolean r1IsVar(MidCodeEntry midCodeEntry) {
        OpType opType = midCodeEntry.getOpType();
        if (isVar(midCodeEntry.getR1()) &&
                (opType == OpType.PUSH_PARAM || opType == OpType.ASSIGN
                        || opType == OpType.ADD || opType == OpType.SUB || opType == OpType.MULT
                        || opType == OpType.DIV || opType == OpType.MOD || opType == OpType.NEG
                        || opType == OpType.SLT || opType == OpType.SLE || opType == OpType.SGT
                        || opType == OpType.SGE || opType == OpType.SEQ || opType == OpType.SNE
                        || opType == OpType.NOT || opType == OpType.BEQZ || opType == OpType.BNEZ)) {
            return true;
        }
        return false;
    }

    public boolean r2IsVar(MidCodeEntry midCodeEntry) {
        OpType opType = midCodeEntry.getOpType();
        if (isVar(midCodeEntry.getR2()) &&
                (opType == OpType.LOAD_ARRAY_1D || opType == OpType.STORE_ARRAY_1D || opType == OpType.LOAD_ARRAY_2D
                || opType == OpType.STORE_ARRAY_2D || opType == OpType.LOAD_ARRDESS
                || opType == OpType.ADD || opType == OpType.SUB || opType == OpType.MULT
                || opType == OpType.DIV || opType == OpType.MOD
                || opType == OpType.SLT || opType == OpType.SLE || opType == OpType.SGT
                || opType == OpType.SGE || opType == OpType.SEQ || opType == OpType.SNE)) {
            return true;
        }
        return false;
    }

    public boolean r3IsVar(MidCodeEntry midCodeEntry) {
        OpType opType = midCodeEntry.getOpType();
        if (isVar(midCodeEntry.getR3()) &&
                (opType == OpType.LOAD_ARRAY_2D || opType == OpType.STORE_ARRAY_2D )) {
            return true;
        }
        return false;
    }

    public boolean dstIsVar(MidCodeEntry midCodeEntry) {
        OpType opType = midCodeEntry.getOpType();
        if (isVar(midCodeEntry.getDst()) &&
                (opType == OpType.ASSIGN || opType == OpType.PRINT_INT || opType == OpType.RET_VALUE)) {
            return true;
        }
        return false;
    }
    //Todo t分配


    @Override
    public String toString() {
        String results = String.format("#~~~~~~~~~~~~~~~~Block%d~~~~~~~~~~~~~~~~\n", blockNum);
        for (String label:labels) {
            results += String.format("LABEL_GEN %s\n",label);
        }
        for (int i = 0; i < midCodeList.size(); i++) {
            results += midCodeList.get(i).toString() + "\n";
        }
        return results;
    }
}
