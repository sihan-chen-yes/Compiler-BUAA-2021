package Optimizer;

import Enum.*;
import GrammarAnalysis.SymbolTable;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicBlock {
    private ArrayList<BasicBlock> preBlocks = new ArrayList<>();
    private ArrayList<MidCodeEntry> midCodeList = new ArrayList<>();
    private ArrayList<MidCodeEntry> assignMid = new ArrayList<>();
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
    private FuncBlock fatherBlock;

    private HashMap<String, String> spreadMap = new HashMap<>();

    public void addMideCodeEntry(MidCodeEntry midCodeEntry) {
        midCodeList.add(midCodeEntry);
        midCodeEntry.setBasicBlock(this);
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

    public FuncBlock getFatherBlock() {
        return fatherBlock;
    }

    public void setFatherBlock(FuncBlock fatherBlock) {
        this.fatherBlock = fatherBlock;
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

    public void genUseDefSet() {
        for (MidCodeEntry midCodeEntry:midCodeList) {
            if (midCodeEntry.getOpType() == OpType.LOAD_ARRDESS) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }


                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
            } else if (midCodeEntry.getOpType() == OpType.STORE_RET) {
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }
            } else if (midCodeEntry.getOpType() == OpType.PUSH_PARAM
                    || midCodeEntry.getOpType() == OpType.BEQZ || midCodeEntry.getOpType() == OpType.BNEZ) {
                if (needInUseSet(midCodeEntry.getR1())) {
                    useSet.add(midCodeEntry.getR1());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR1())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR1());
                }


                if (isGlobalVar(midCodeEntry.getR1())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR1());
                }
            } else if (midCodeEntry.getOpType() == OpType.NEG || midCodeEntry.getOpType() == OpType.NOT) {
                if (needInUseSet(midCodeEntry.getR1())) {
                    useSet.add(midCodeEntry.getR1());
                }
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR1())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR1());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }

                if (isGlobalVar(midCodeEntry.getR1())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR1());
                }
            } else if ((midCodeEntry.getOpType() == OpType.LOAD_ARRAY_1D)) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }


                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
            } else if (midCodeEntry.getOpType() == OpType.STORE_ARRAY_1D) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addUseSet(midCodeEntry.getDst());
                }


                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
            } else if (midCodeEntry.getOpType() == OpType.LOAD_ARRAY_2D) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInUseSet(midCodeEntry.getR3())) {
                    useSet.add(midCodeEntry.getR3());
                }
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR3())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR3());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }


                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
                if (isGlobalVar(midCodeEntry.getR3())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR3());
                }
            } else if (midCodeEntry.getOpType() == OpType.STORE_ARRAY_2D) {
                if (needInUseSet(midCodeEntry.getR2())) {
                    useSet.add(midCodeEntry.getR2());
                }
                if (needInUseSet(midCodeEntry.getR3())) {
                    useSet.add(midCodeEntry.getR3());
                }
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR3())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR3());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addUseSet(midCodeEntry.getDst());
                }


                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
                if (isGlobalVar(midCodeEntry.getR3())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR3());
                }
            } else if (midCodeEntry.getOpType() == OpType.ASSIGN) {
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
                if (needInDefSet(midCodeEntry.getR1())) {
                    defSet.add(midCodeEntry.getR1());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addUseSet(midCodeEntry.getDst());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getR1())) {
                    midCodeEntry.addDefSet(midCodeEntry.getR1());
                }

                if (isGlobalVar(midCodeEntry.getDst())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getDst());
                }
                if (isGlobalVar(midCodeEntry.getR1())) {
                    midCodeEntry.addDefGlobal(midCodeEntry.getR1());
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
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR1())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR1());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getR2())) {
                    midCodeEntry.addUseSet(midCodeEntry.getR2());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }

                if (isGlobalVar(midCodeEntry.getR1())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR1());
                }
                if (isGlobalVar(midCodeEntry.getR2())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getR2());
                }
            } else if (midCodeEntry.getOpType() == OpType.PRINT_INT || midCodeEntry.getOpType() == OpType.RET_VALUE) {
                if (needInUseSet(midCodeEntry.getDst())) {
                    useSet.add(midCodeEntry.getDst());
                }
                if (needInUseSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addUseSet(midCodeEntry.getDst());
                }

                if (isGlobalVar(midCodeEntry.getDst())) {
                    midCodeEntry.addUseGlobal(midCodeEntry.getDst());
                }
            } else if (midCodeEntry.getOpType() == OpType.GETINT) {
                if (needInDefSet(midCodeEntry.getDst())) {
                    defSet.add(midCodeEntry.getDst());
                }
                if (needInDefSetMid(midCodeEntry,midCodeEntry.getDst())) {
                    midCodeEntry.addDefSet(midCodeEntry.getDst());
                }
            }
        }
    }

    public boolean needInUseSet(String name) {
        return name != null && MidCodeGener.getSymbolTable().search_local(func,name) != null
                && isLocalVar(name) && !defSet.contains(name);
    }

    public boolean needInUseSetMid(MidCodeEntry midCodeEntry,String name) {
        return name != null && MidCodeGener.getSymbolTable().search_local(func,name) != null
                && isLocalVar(name) && !midCodeEntry.getDefSet().contains(name);
    }

    public boolean needInDefSet(String name) {
        return name != null && MidCodeGener.getSymbolTable().search_local(func,name) != null
                && isLocalVar(name) && !useSet.contains(name);
    }

    public boolean needInDefSetMid(MidCodeEntry midCodeEntry,String name) {
        return name != null && MidCodeGener.getSymbolTable().search_local(func,name) != null
                && isLocalVar(name) && !midCodeEntry.getUseSet().contains(name);
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

    public boolean isLocalVar(String name) {
        return MidCodeGener.getSymbolTable().search_local(func,name).getDataType() == DataType.INT
                && !isNumber(name);
        //Todo 考虑use def时考虑参数 但是不分配reg给他
    }

    public boolean isGlobalVar(String name) {
        return !isNumber(name) && MidCodeGener.getSymbolTable().search_local(func,name) == null
                && MidCodeGener.getSymbolTable().search_global(name) != null;
    }

    public ArrayList<MidCodeEntry> getOptimizedMidCode() {
        ArrayList<MidCodeEntry> midCodeEntries = new ArrayList<>();
        for (String label:labels) {
            midCodeEntries.add(new MidCodeEntry(OpType.LABEL_GEN,null,null,null,label));
        }
        midCodeEntries.addAll(midCodeList);
        return midCodeEntries;
    }

    public ArrayList<MidCodeEntry> getMidCode() {
        ArrayList<MidCodeEntry> midCodeEntries = new ArrayList<>();
        for (String label:labels) {
            midCodeEntries.add(new MidCodeEntry(OpType.LABEL_GEN,null,null,null,label));
        }
        midCodeEntries.addAll(midCodeList);
        return midCodeEntries;
    }

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

    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getFunc() {
        return func;
    }

    public void genSubConf(ConflictGraph conflictGraph) {
        for (MidCodeEntry midCodeEntry:midCodeList) {
            //如果是参数则不分配reg
            HashSet<String> useDefOutSet = midCodeEntry.getUseDefOutSet();
            HashSet<String> tmp = new HashSet<>();
            SymbolTable symbolTable = MidCodeGener.getSymbolTable();
            for (String varOut:useDefOutSet) {
                if (symbolTable.search_param(func,varOut) == null) {
                    //不是参数
                    tmp.add(varOut);
                }
            }
            conflictGraph.addLiveVars(tmp);
        }
    }

    public void genUseDef() {
        HashSet<String> in = useDefOutSet;
        for (int i = midCodeList.size() - 1;i >= 0;i--) {
            HashSet<String> out = new HashSet<>(in);
            midCodeList.get(i).setUseDefOutSet(out);
            HashSet<String> tmp = new HashSet<>(out);
            tmp.removeAll(midCodeList.get(i).getDefSet());
            tmp.addAll(midCodeList.get(i).getUseSet());
            midCodeList.get(i).setUseDefInSet(tmp);
            in = tmp;
        }
    }

    public void delDeadCode() {
        ArrayList<MidCodeEntry> tmp = new ArrayList<>();
        for (MidCodeEntry midCodeEntry:midCodeList) {
            OpType opType = midCodeEntry.getOpType();
            assert opType != OpType.LABEL_GEN;
            String r1 = midCodeEntry.getR1();
            String dst = midCodeEntry.getDst();
            SymbolTable symbolTable = MidCodeGener.getSymbolTable();
            if (opType == OpType.STORE_RET || opType == OpType.LOAD_ARRAY_1D || opType == OpType.LOAD_ARRAY_2D
                    || opType == OpType.LOAD_ARRDESS
                    || opType == OpType.ADD || opType == OpType.SUB
                    || opType == OpType.MULT || opType == OpType.DIV || opType == OpType.MOD
                    || opType == OpType.NEG || opType == OpType.SLT || opType == OpType.SLE
                    || opType == OpType.SGT || opType == OpType.SGE || opType == OpType.SEQ
                    || opType == OpType.SNE || opType == OpType.NOT) {
                if (midCodeEntry.getUseDefOutSet().contains(dst)) {
                    //新定义的dst 在out集中 需要加入
                    tmp.add(midCodeEntry);
                }
            } else if (opType == OpType.ASSIGN) {
                //先检查是否是全局变量
                if (symbolTable.search_local(func,r1) != null) {
                    if (midCodeEntry.getUseDefOutSet().contains(r1)) {
                        //新定义的r1 在out集中 需要加入
                        tmp.add(midCodeEntry);
                    }
                } else {
                    assert symbolTable.search_global(r1) != null;
                    //是全局变量 无条件加入
                    tmp.add(midCodeEntry);
                }
            } else {
                tmp.add(midCodeEntry);
            }
        }
        midCodeList = tmp;
    }

    public void spread() {
        //顺序扫描 建立映射之后放入midCodeList 再DF分析
        for (MidCodeEntry midCodeEntry:midCodeList) {
            //每次先替换再扩充映射
            OpType opType = midCodeEntry.getOpType();
            String r1 = midCodeEntry.getR1();
            String r2 = midCodeEntry.getR2();
            String dst = midCodeEntry.getDst();
            if (opType == OpType.PUSH_PARAM) {
                if (spreadMap.containsKey(r1)) {
                    midCodeEntry.setR1(spreadMap.get(r1));
                }
            } else if (opType == OpType.LOAD_ARRAY_1D || opType == OpType.STORE_ARRAY_1D
                    || opType == OpType.LOAD_ARRDESS && r2 != null) {
                if (spreadMap.containsKey(r2)) {
                    midCodeEntry.setR2(spreadMap.get(r2));
                }
            } else if (opType == OpType.LOAD_ARRAY_2D || opType == OpType.STORE_ARRAY_2D) {
                if (spreadMap.containsKey(r1)) {
                    midCodeEntry.setR1(spreadMap.get(r1));
                }
                if (spreadMap.containsKey(r2)) {
                    midCodeEntry.setR2(spreadMap.get(r2));
                }
            } else if (opType == OpType.ASSIGN) {
                if (spreadMap.containsKey(dst)) {
                    midCodeEntry.setDst(spreadMap.get(dst));
                }
                dst = midCodeEntry.getDst();
                spreadMap.put(r1,dst);
            } else if (opType == OpType.PRINT_INT) {
                if (spreadMap.containsKey(dst)) {
                    midCodeEntry.setDst(spreadMap.get(dst));
                }
            } else if (opType == OpType.RET_VALUE) {
                if (spreadMap.containsKey(dst)) {
                    midCodeEntry.setDst(spreadMap.get(dst));
                }
            } else if (isDoubleOp(opType)) {
                spreadConst(midCodeEntry);
            } else if (opType == OpType.BEQZ || opType == OpType.BNEZ) {
                if (spreadMap.containsKey(r1)) {
                    midCodeEntry.setR1(spreadMap.get(r1));
                }
            }
        }
    }

    public void spreadConst(MidCodeEntry midCodeEntry) {
        String r1,r2,dst;
        spreadR1R2(midCodeEntry);
        r1 = midCodeEntry.getR1();
        r2 = midCodeEntry.getR2();
        //得到最新的操作数
        dst = midCodeEntry.getDst();
        OpType opType = midCodeEntry.getOpType();
        if (isNumber(r1) && isNumber(r2)) {
            int val;
            if (opType == OpType.ADD) {
                val = Integer.valueOf(r1) + Integer.valueOf(r2);
            } else if (opType == OpType.SUB) {
                val = Integer.valueOf(r1) - Integer.valueOf(r2);
            } else if (opType == OpType.MULT) {
                val = Integer.valueOf(r1) * Integer.valueOf(r2);
            } else if (opType == OpType.DIV) {
                val = Integer.valueOf(r1) / Integer.valueOf(r2);
            } else if (opType == OpType.MOD) {
                val = Integer.valueOf(r1) % Integer.valueOf(r2);
            } else if (opType == OpType.SLT) {
                if (Integer.valueOf(r1) < Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            } else if (opType == OpType.SLE) {
                if (Integer.valueOf(r1) <= Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            } else if (opType == OpType.SGT) {
                if (Integer.valueOf(r1) > Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            } else if (opType == OpType.SGE) {
                if (Integer.valueOf(r1) >= Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            } else if (opType == OpType.SEQ) {
                if (Integer.valueOf(r1) == Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            } else {
                assert opType == OpType.SNE;
                if (Integer.valueOf(r1) != Integer.valueOf(r2)) {
                    val = 1;
                } else {
                    val = 0;
                }
            }
            spreadMap.put(dst,String.valueOf(val));
        } else {
            spreadMap.remove(dst);
        }
    }

    public boolean isDoubleOp(OpType opType) {
        if (opType == OpType.ADD || opType == OpType.SUB || opType == OpType.MULT || opType == OpType.DIV ||
                opType == OpType.MOD ||
                opType == OpType.SLT || opType == OpType.SLE || opType == OpType.SGT || opType == OpType.SGE ||
                opType == OpType.SEQ || opType == OpType.SNE) {
            return true;
        }
        return false;
    }

    public void spreadR1R2(MidCodeEntry midCodeEntry) {
        String r1 = midCodeEntry.getR1();
        String r2 = midCodeEntry.getR2();
        if (spreadMap.containsKey(r1)) {
            midCodeEntry.setR1(spreadMap.get(r1));
        }
        if (spreadMap.containsKey(r2)) {
            midCodeEntry.setR2(spreadMap.get(r2));
        }
    }

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
