import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable {
    private ArrayList<SymbolTableEntry> global = new ArrayList<>();
    private HashMap<String,ArrayList<SymbolTableEntry>> funcToDecl = new HashMap<>();
    private ArrayList<SymbolTableEntry> tmp = new ArrayList<>();
    private ErrorAnalysis errorAnalysis;

    public SymbolTable(ErrorAnalysis errorAnalysis) {
        this.errorAnalysis = errorAnalysis;
    }

    public void finFuncDef(String funcName) {
        if (!funcToDecl.containsKey(funcName)) {
            funcToDecl.put(funcName,tmp);
        }
        tmp = new ArrayList<>();
    }

    public void insertGlobal(SymbolTableEntry symbolTableEntry) {
        if (queryDefined(symbolTableEntry.getName())) {
            errorAnalysis.addError(symbolTableEntry.getLine(),ErrorType.reDef);
        } else {
            global.add(symbolTableEntry);
        }
    }

    public void insertLocal(SymbolTableEntry symbolTableEntry,String funcName) {
        int layer = symbolTableEntry.getLayer();
        if (queryLocalReDefined(symbolTableEntry.getName(), funcName,layer)) {
            errorAnalysis.addError(symbolTableEntry.getLine(), ErrorType.reDef);
        } else {
            tmp.add(symbolTableEntry);
        }
    }

    public void removeLocal(int layer,String funcName) {
        Iterator iterator = tmp.iterator();
        while (iterator.hasNext()) {
            SymbolTableEntry entry = (SymbolTableEntry) iterator.next();
            if (entry.getLayer() == layer && entry.getDeclType() != DeclType.PARAM) {
                iterator.remove();
            }
        }
    }

    public boolean queryDefined(String name) {
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(name)) {
                return true;
                //存在
            }
        }
        return false;
    }

    public boolean queryLocalReDefined(String name,String funcName,int layer) {
        for (SymbolTableEntry symbolTableEntry:tmp) {
            if (symbolTableEntry.getName().equals(name) && layer == symbolTableEntry.getLayer()) {
                return true;
            }
        }
        return false;
    }

     public ArrayList<SymbolTableEntry> getLocalTable(String funcName) {
         return tmp;
     }

    public boolean queryLocalDefined(String name, String funcName) {
        assert funcToDecl.containsKey(funcName);
        for (SymbolTableEntry symbolTableEntry:tmp) {
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

    public void queryFuncParamNum(Word Ident,Attribute attribute) {
        String funcName = Ident.getWord();
        assert funcToDecl.containsKey(funcName);
        int paramNum = 0;
        ArrayList<SymbolTableEntry> localTable = funcToDecl.get(funcName);
        for (SymbolTableEntry symbolTableEntry:localTable) {
            if (symbolTableEntry.getDeclType() == DeclType.PARAM) {
                paramNum++;
            } else {
                break;
            }
        }
        if (paramNum != attribute.getRParamNum()) {
            errorAnalysis.addError(Ident.getLine(),ErrorType.paramsNumError);
        }
    }

    public void queryFuncParamType(Word Ident,Attribute attribute) {
        String funcName = Ident.getWord();
        assert funcToDecl.containsKey(funcName);
        ArrayList<SymbolTableEntry> localTable = funcToDecl.get(funcName);
        ArrayList<DataType> RParamTypes = attribute.getRParamTypes();
        for (int i = 0; i < RParamTypes.size(); i++) {
            if (RParamTypes.get(i) != localTable.get(i).getDataType()) {
                errorAnalysis.addError(Ident.getLine(),ErrorType.paramsTypeError);
                return;
            }
        }
    }

    public boolean isConst(String funcName,String Ident) {
        assert funcToDecl.containsKey(funcName);
        for (int i = tmp.size() - 1;i >= 0;i--) {
            if (tmp.get(i).getName().equals(Ident)) {
                if (tmp.get(i).getDeclType() == DeclType.CONST) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        for (SymbolTableEntry symbolTableEntry:global) {
            if (symbolTableEntry.getName().equals(Ident) && symbolTableEntry.getDeclType() == DeclType.CONST) {
                return true;
            }
        }
        return false;//未定义函数
    }

    public ArrayList<SymbolTableEntry> getGlobal() {
        return global;
    }
}
