package Optimizer;

import java.util.ArrayList;

public class LiveVar {
    private String var;
    private ArrayList<LiveVar> conflictVar = new ArrayList<>();

    public LiveVar(String var) {
        this.var = var;
    }

    public void addConflictVar(LiveVar var) {
        if (!conflictVar.contains(var)) {
            conflictVar.add(var);
            var.addConflictVar(this);
        }
    }
}
