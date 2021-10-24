package GrammarAnalysis;

import ASTNode.FuncFParam;
import Enum.DataType;
import Enum.DeclType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class SymbolTable {
    private ArrayList<SymbolTableEntry> global = new ArrayList<>();
    private HashMap<String,ArrayList<SymbolTableEntry>> funcToDecl = new HashMap<>();

    public boolean insertGlobal(SymbolTableEntry symbolTableEntry) {
        if (queryGlobalDefined(symbolTableEntry)) {
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

    public boolean queryGlobalDefined(SymbolTableEntry symbolTableEntry) {
        //变量或者函数是否重复声明
        for (SymbolTableEntry entry:global) {
            if (symbolTableEntry.getName().equals(entry.getName()) && isSameDecl(entry,symbolTableEntry)) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean isSameDecl(SymbolTableEntry entry1,SymbolTableEntry entry2) {
        //种类相同
        return isFunc(entry1) == isFunc(entry2);
    }

    public boolean isData(SymbolTableEntry symbolTableEntry) {
        return !isFunc(symbolTableEntry);
    }

    public boolean isFunc(SymbolTableEntry symbolTableEntry) {
        return symbolTableEntry.getDeclType() == DeclType.FUNC;
    }

    public DataType queryFuncReturn(String name) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name) && isFunc(symbolTableEntry)) {
                return symbolTableEntry.getDataType();
                //存在
            }
        }
        return DataType.UNDEFINED;
    }

    public boolean queryLocalReDefined(String name,String funcName,int layer) {
        //局部变量是否重复声明
        //和参数重名
        if (layer == 1) {
            ArrayList<FuncFParam> funcFParams = queryFuncFParam(funcName);
            for (FuncFParam funcFParam:funcFParams) {
                if (name.equals(funcFParam.getName())) {
                    return true;
                }
            }
        }
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name) && layer == symbolTableEntry.getLayer()) {
                return true;
            }
        }
        return false;
    }

    public boolean queryLocalDefined(String name, String funcName) {
        //局部变量是否声明
        assert funcToDecl.containsKey(funcName);
        for (SymbolTableEntry symbolTableEntry:funcToDecl.get(funcName)) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
            }
        }
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name) && !isFunc(symbolTableEntry)) {
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

    public boolean queryFunc(String funcName) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(funcName) && symbolTableEntry.getDeclType() == DeclType.FUNC) {
                return true;
            }
        }
        return false;
    }

    public DataType queryGlobalDataType(String name) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name)) {
                return symbolTableEntry.getDataType();
            }
        }
        return DataType.UNDEFINED;
    }


    public DataType queryLocalDataType(String name,String funcName) {
        for (int i = funcToDecl.get(funcName).size() - 1;i >= 0;i--) {
            if (funcToDecl.get(funcName).get(i).getName().equals(name)) {
                return funcToDecl.get(funcName).get(i).getDataType();
            }
        }
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(funcName)) {
                ArrayList<FuncFParam> funcFParams = queryFuncFParam(funcName);
                for (FuncFParam funcFParam:funcFParams) {
                    if (funcFParam.getName().equals(name)) {
                        return funcFParam.getDataType();
                    }
                }
            }
        }
        return queryGlobalDataType(name);
    }

    public ArrayList<FuncFParam> queryFuncFParam(String funcName) {
        ArrayList<FuncFParam> fParams = null;
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
