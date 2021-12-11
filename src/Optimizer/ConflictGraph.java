package Optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ConflictGraph {
    private ArrayList<LiveVar> liveVars = new ArrayList<>();
    private HashMap<String, String> varToReg = new HashMap<>();
    private ArrayList<LiveVar> tmp = new ArrayList<>();
    private ArrayList<LiveVar> deleted = new ArrayList<>();

    private ArrayList<String> regs = new ArrayList<>();


    public ConflictGraph() {
        resetRegs();
    }

    public void addLiveVars(HashSet<String> vars) {
        ArrayList<LiveVar> tmp = new ArrayList<>();
        for (String var:vars) {
            LiveVar liveVar = search(new LiveVar(var));
            tmp.add(liveVar);
        }
        for (int i = 0;i < tmp.size() - 1;i++) {
            for (int j = i + 1;j < tmp.size();j++) {
                tmp.get(i).addConflictVar(tmp.get(j));
            }
        }
        for (int i = 0;i < tmp.size();i++) {
            if (!liveVars.contains(tmp.get(i))) {
                liveVars.add(tmp.get(i));
            }
        }
    }

    public void resetRegs() {
        for (int i = 0; i < 8;i++) {
            regs.add(String.format("$s%d",i));
        }
    }

    public HashMap<String, String> getVarToReg() {
        return varToReg;
    }

    public void dye() {
        int originNum = liveVars.size();
        while ((tmp.size() + deleted.size()) != originNum) {
            boolean find = false;
            for (int i = 0;i < liveVars.size();i++) {
                LiveVar liveVar = liveVars.get(i);
                if (liveVar.getEdgeNum() < regs.size()) {
                    liveVar.remove();
                    tmp.add(liveVar);
                    liveVars.remove(i);
                    find = true;
                    break;
                }
            }
            if (!find) {
                for (int i = 0;i < liveVars.size();i++) {
                    LiveVar liveVar = liveVars.get(i);
                    if (liveVar.getEdgeNum() >= regs.size()) {
                        liveVar.delete();
                        deleted.add(liveVar);
                        liveVars.remove(i);
                        break;
                    }
                }
            }
        }
        for (int i = tmp.size() - 1;i >= 0;i--) {
            LiveVar liveVar = tmp.get(i);
            HashSet<String> regs = liveVar.getEdgeReg();
            for (String reg: this.regs) {
                if (!regs.contains(reg)) {
                    liveVar.setReg(reg);
                    varToReg.put(liveVar.getVar(),reg);
                    liveVar.back();
                    liveVars.add(liveVar);
                    break;
                }
            }
        }
    }

    public LiveVar search(LiveVar var) {
        for (LiveVar liveVar:liveVars) {
            if (liveVar.equals(var)) {
                return liveVar;
            }
        }
        return var;
    }

    public boolean hasReg(String var) {
        return varToReg.containsKey(var);
    }

    public String getReg(String var) {
        return varToReg.get(var);
    }
}
