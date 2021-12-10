package Optimizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class LiveVar {
    private String var;
    private ArrayList<LiveVar> conflictVar = new ArrayList<>();
    private boolean hasReg = false;
    private boolean removed = false;
    private boolean deleted = false;
    private String reg;

    public LiveVar(String var) {
        this.var = var;
    }

    public void addConflictVar(LiveVar var) {
        if (!conflictVar.contains(var)) {
            conflictVar.add(var);
            var.addConflictVar(this);
        }
    }

    public boolean hasReg() {
        return hasReg;
    }

    public String getVar() {
        return var;
    }

    public int getEdgeNum() {
        return conflictVar.size();
    }

    public void setReg(boolean hasReg) {
        this.hasReg = hasReg;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public void delete() {
        removed = true;
        deleted = true;
        hasReg = false;
    }

    public void remove() {
        removed = true;
        deleted = false;
        hasReg = false;
    }

    public void back() {
        removed = false;
        hasReg = true;
    }

    public HashSet<String> getEdgeReg() {
        HashSet<String> regs = new HashSet<>();
        for (LiveVar liveVar:conflictVar) {
            if (!liveVar.deleted && !liveVar.removed) {
                regs.add(liveVar.getReg());
            }
        }
        return regs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveVar liveVar = (LiveVar) o;
        return var.equals(liveVar.var);
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }
}
