import java.util.ArrayList;
import java.util.HashMap;

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


}
