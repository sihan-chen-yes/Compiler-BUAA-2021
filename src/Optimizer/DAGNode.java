package Optimizer;
import Enum.OpType;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DAGNode {
    private ArrayList<String> alias = new ArrayList<>();
    //包括T a b c 常数
    private int nodeNum;

    private OpType opType = OpType.Operand;
    //计算型节点的操作类型
    private ArrayList<DAGNode> fathers = new ArrayList<>();
    private DAGNode left = null;
    private DAGNode middle = null;
    //如果是一维数组就为null
    private DAGNode right = null;

    public DAGNode(String name,int num) {
        //操作数型节点
        alias.add(name);
        nodeNum = num;
    }

    public void addAlias(String name) {
        if (!alias.contains(name)) {
            alias.add(name);
        }
    }

    public DAGNode(OpType opType,int num) {
        //计算型节点
        this.opType = opType;
        this.nodeNum = num;
    }

    public void addFatherNode(DAGNode dagNode) {
        this.fathers.add(dagNode);
    }

    public void setLeftNode(DAGNode dagNode) {
        this.left = dagNode;
        dagNode.addFatherNode(this);
    }

    public void setMiddleNode(DAGNode dagNode) {
        this.middle = dagNode;
        dagNode.addFatherNode(this);
    }

    public void setRightNode(DAGNode dagNode) {
        this.right = dagNode;
        dagNode.addFatherNode(this);
    }

    public String getChange(ArrayList<String> deletedOperand) {
        Pattern constPattern = Pattern.compile("\\d*");
        Matcher matcher;
        for (String name:alias) {
            matcher = constPattern.matcher(name);
            if (matcher.matches()) {
                return name;
            }
        }
        Pattern tempPattern = Pattern.compile("^@T_\\d*");
        for (String name:alias) {
            matcher = tempPattern.matcher(name);
            if (!deletedOperand.contains(name) && matcher.matches()) {
                return name;
            }
        }
        return null;
    }

    public int getNodeNum() {
        return nodeNum;
    }

    public OpType getOpType() {
        return opType;
    }

    public int getLeftNodeNum() {
        return left.getNodeNum();
    }

    public int getMiddleNodeNum() {
        return middle.getNodeNum();
    }

    public int getRightNodeNum() {
        return right.getNodeNum();
    }

}
