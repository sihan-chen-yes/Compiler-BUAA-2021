package GrammarAnalysis;

import ASTNode.FuncFParam;
import Enum.DataType;
import Enum.DeclType;
import WordAnalysis.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
public class SymbolTable {
    private ArrayList<SymbolTableEntry> global = new ArrayList<>();
    private HashMap<String,ArrayList<SymbolTableEntry>> funcToDecl = new HashMap<>();
    //需要remove
    private HashMap<String,ArrayList<SymbolTableEntry>> fullFunc = new HashMap<>();
    //没有remove过的full符号表
    private static int offset_gp = -0x8000;
    private static int offset_sp = 4;

//错误处理！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    public boolean insertGlobal(SymbolTableEntry symbolTableEntry) {
        if (queryGlobalDefined(symbolTableEntry)) {
            return false;
        } else {
            global.add(symbolTableEntry);
            if (symbolTableEntry.getDeclType() == DeclType.FUNC) {
                funcToDecl.put(symbolTableEntry.getName(),new ArrayList<>());
                fullFunc.put(symbolTableEntry.getName(),new ArrayList<>());
            }
            return true;
        }
    }

    public boolean insertLocal(SymbolTableEntry symbolTableEntry, String funcName) {
        assert funcToDecl.containsKey(funcName);
        int layer = symbolTableEntry.getLayer();
        if (queryLocalReDefined(symbolTableEntry.getName(), funcName,layer)) {
            return false;
        } else {
            funcToDecl.get(funcName).add(symbolTableEntry);
            fullFunc.get(funcName).add(symbolTableEntry);
            return true;
        }
    }

    public void removeLocal(int layer, String funcName) {
        //错误处理时使用
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

    public boolean queryLocalReDefined(String name, String funcName, int layer) {
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

    public DataType queryLocalDataType(String name, String funcName) {
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

    public boolean isConst(String funcName, String Ident) {
        //错误检查时使用
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
        ArrayList<FuncFParam> funcFParams = queryFuncFParam(funcName);
        for (FuncFParam funcFParam:funcFParams) {
            if (funcFParam.getName().equals(Ident)) {
                return false;
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
//错误处理！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
    public SymbolTableEntry search_global(Word word) {
        //生成目标代码
        SymbolTableEntry symbolTableEntry = null;
        for (int i = global.size() - 1;i >= 0;i--) {
            if (global.get(i).getName().equals(word.getWord())
                     && global.get(i).getDeclType() != DeclType.FUNC) {
                symbolTableEntry = global.get(i);
            }
        }
        assert symbolTableEntry != null;
        return symbolTableEntry;
    }

    public SymbolTableEntry search_local(String func, Word word) {
        //生成目标代码
        ArrayList<SymbolTableEntry> local = fullFunc.get(func);
        SymbolTableEntry symbolTableEntry = null;
        for (int i = local.size() - 1;i >= 0;i--) {
            if (local.get(i).getName().equals(word.getWord())) {
                symbolTableEntry = local.get(i);
                return symbolTableEntry;
            }
        }
        symbolTableEntry = search_global(word);
        assert symbolTableEntry != null;
        return symbolTableEntry;
    }

    public SymbolTableEntry searchDefinedEntry(String func, Word word) {
        //编译时利用符号表求值
        SymbolTableEntry symbolTableEntry = null;
        if (func != null) {
            ArrayList<SymbolTableEntry> local = funcToDecl.get(func);
            for (int i = local.size() - 1;i >= 0;i--) {
                if (local.get(i).getName().equals(word.getWord())) {
                    symbolTableEntry = local.get(i);
                    return symbolTableEntry;
                }
            }
        }
        for (int i = global.size() - 1;i >= 0;i--) {
            if (global.get(i).getName().equals(word.getWord()) && global.get(i).getDeclType() != DeclType.FUNC) {
                symbolTableEntry = global.get(i);
            }
        }
        assert symbolTableEntry != null;
        return symbolTableEntry;
    }

    public String getRefactorName(String funcName,Word word) {
        return word.getWord() + searchDefinedEntry(funcName,word).getLine();
    }

    public static int getOffset_gp() {
        return offset_gp;
    }

    public static void setOffset_gp(int offset_gp) {
        SymbolTable.offset_gp = offset_gp;
    }

    public void setLocalAddr(String func) {
        ArrayList<SymbolTableEntry> local = fullFunc.get(func);
        for (int i = local.size() - 1;i >= 0;i--) {
            local.get(i).setspAddr();
        }
    }

    public static int getOffset_sp() {
        return offset_sp;
    }

    public static void setOffset_sp(int offset_sp) {
        SymbolTable.offset_sp = offset_sp;
    }

    public void reset() {
        offset_sp = 0;
    }

    public int getLocalSize(String func) {
        ArrayList<SymbolTableEntry> local = fullFunc.get(func);
        int size = 0;
        for (int i = 0;i < local.size() - 1;i++) {
            size += local.get(i).getSize();
        }
        return size + 4;
        //最开始还有一个 ra
    }

    public int getRaAddr(String func) {
        return getLocalSize(func) - 4;
    }

    public void refactorName(String func) {
        ArrayList<SymbolTableEntry> local = fullFunc.get(func);
        for (int i = 0;i < local.size() - 1;i++) {
            if (local.get(i).getDeclType() != DeclType.TEMP) {
                local.get(i).refactorName();
            }
        }
    }

    public void insertLocalTemp(String temp,String funcName) {
        SymbolTableEntry symbolTableEntry = new SymbolTableEntry(
                new Word("TEMP",temp,-1), DeclType.TEMP,DataType.INT);
        symbolTableEntry.setSize();
        fullFunc.get(funcName).add(symbolTableEntry);
    }
}
