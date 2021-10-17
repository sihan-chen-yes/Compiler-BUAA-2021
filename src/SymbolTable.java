import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable {
    private ArrayList<SymbolTableEntry> globalDecl = new ArrayList<>();
    private ArrayList<SymbolTableEntry> globalFunc = new ArrayList<>();
    private HashMap<String,ArrayList<SymbolTableEntry>> funcToDecl = new HashMap<>();
    private int layer = 0;

    enum declType {
        VAR,CONST
    }

    enum retType {
        INT, VOID
    }

    public void insertDecl(SymbolTableEntry symbolTableEntry) {
        globalDecl.add(symbolTableEntry);
    }

    public void insertFunc(SymbolTableEntry symbolTableEntry) {
        globalFunc.add(symbolTableEntry);
    }

    public void insertLocalDecl(SymbolTableEntry symbolTableEntry,String funcName) {
        ArrayList<SymbolTableEntry> localDeclTable;
        if (funcToDecl.containsKey(funcName)) {
            localDeclTable = funcToDecl.get(funcName);
        } else {
            localDeclTable = new ArrayList<>();
            funcToDecl.put(funcName,localDeclTable);
        }
        localDeclTable.add(symbolTableEntry);
    }

    public void removeLocalDecl(int layer,String funcName) {
        ArrayList localDeclTable = funcToDecl.get(funcName);
        Iterator iterator = localDeclTable.iterator();
        while (iterator.hasNext()) {
            SymbolTableEntry entry = (SymbolTableEntry) iterator;
            if (entry.getLayer() == layer) {
                iterator.remove();
            }
        }
    }

    public boolean queryDecl(String name) {
        for (SymbolTableEntry symbolTableEntry:globalDecl) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean queryFunc(String name) {
        for (SymbolTableEntry symbolTableEntry:globalFunc) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean queryLocalReDecl(String name,String funcName,int layer) {
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name) && layer == symbolTableEntry.getLayer()) {
                return true;
            }
        }
        return false;
    }

    public boolean queryLocalDecl(String name, String funcName) {
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
