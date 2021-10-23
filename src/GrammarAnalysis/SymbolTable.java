package GrammarAnalysis;

import Enum.DeclType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable {
    private ArrayList<SymbolTableEntry> global = new ArrayList<>();
    private HashMap<String,ArrayList<SymbolTableEntry>> funcToDecl = new HashMap<>();

    public boolean insertGlobal(SymbolTableEntry symbolTableEntry) {
        if (queryGlobalDefined(symbolTableEntry.getName())) {
            return false;
        } else {
            global.add(symbolTableEntry);
            if (symbolTableEntry.getDeclType() == DeclType.FUNC) {
                funcToDecl.put(symbolTableEntry.getName(),new ArrayList<>());
            }
            return true;
        }
    }

    public boolean insertLocal(SymbolTableEntry symbolTableEntry,String funcName) {
        assert funcToDecl.containsKey(funcName);
        int layer = symbolTableEntry.getLayer();
        if (queryLocalReDefined(symbolTableEntry.getName(), funcName,layer)) {
            return false;
        } else {
            funcToDecl.get(funcName).add(symbolTableEntry);
            return true;
        }
    }

    public void removeLocal(int layer,String funcName) {
        Iterator iterator = funcToDecl.get(funcName).iterator();
        while (iterator.hasNext()) {
            SymbolTableEntry entry = (SymbolTableEntry) iterator.next();
            if (entry.getLayer() == layer) {
                iterator.remove();
            }
        }
    }

    public boolean queryGlobalDefined(String name) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean queryFuncDefined(String name) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name) && symbolTableEntry.getDeclType() == DeclType.FUNC) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean queryLocalReDefined(String name,String funcName,int layer) {
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name) && layer == symbolTableEntry.getLayer()) {
                return true;
            }
        }
        return false;
    }

    public boolean queryLocalDefined(String name, String funcName) {
        assert funcToDecl.containsKey(funcName);
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
            }
        }
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name) && symbolTableEntry.getDeclType() != DeclType.FUNC) {
                return true;
            }
        }
        return false;
    }

    public int queryFuncParamNum(String funcName) {
        assert funcToDecl.containsKey(funcName);
        int num = 0;
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(funcName)) {
                num = symbolTableEntry.getFParamNum();
            }
        }
        return num;
    }

    public ArrayList<FParam> queryFuncParam(String funcName) {
        ArrayList<FParam> fParams = null;
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(funcName)) {
                fParams = symbolTableEntry.getFParams();
            }
        }
        return fParams;
    }

    public boolean isConst(String funcName,String Ident) {
        assert funcToDecl.containsKey(funcName);
        for (int i = funcToDecl.get(funcName).size() - 1;i >= 0;i--) {
            if (funcToDecl.get(funcName).get(i).getName().equals(Ident)) {
                if (funcToDecl.get(funcName).get(i).getDeclType() == DeclType.CONST) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(Ident)) {
                if (symbolTableEntry.getDeclType() == DeclType.CONST) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;//未定义函数
    }
}
