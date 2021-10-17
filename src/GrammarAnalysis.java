import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GrammarAnalysis {
    private ArrayList<HashMap<String, String>> wordAndGrammar = new ArrayList<>();
    private HashMap<String, String> symbol = null;
    private Word word = null;
    private Word peekWord = null;
    private ArrayList<Word> wordList;
    private HashMap<String, String> nonTermimal = new HashMap<>();
    private int pos = 0;
    private FileWriter writer;

    public GrammarAnalysis(ArrayList<Word> wordList, File outputFile) {
        this.wordList = wordList;
        try {
            this.writer = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveGrammarAnalysis() {
        try {
            Iterator iterator = wordAndGrammar.iterator();
            HashMap<String, String> symbol;
            while (iterator.hasNext()) {
                symbol = (HashMap<String, String>) iterator.next();
                if (symbol.get("class").equals("NONTER")) {
                    writer.write(String.format("%s\n",symbol.get("word")));
                } else {
                    writer.write(String.format("%s %s\n",symbol.get("class"),symbol.get("word")));
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recursionDown(){
        getSymbol();
        //进来之前symbol里面已经get到了新的symbol 出去的时候必须要getSymbol
        CompUnit();
    }

    public void getSymbol() {
        if (word != null) {
            symbol = new HashMap<>();
            symbol.put("class",word.getClassName());
            symbol.put("word",word.getWord());
            wordAndGrammar.add(symbol);
        }
        if (pos < wordList.size()) {
            word = wordList.get(pos++);
        }
        else {
            word = new Word("EOF","",wordList.get(wordList.size() - 1).getLine());
        }
        //刚开始的时候 symbol为null
    }

    public String getWordClass() {
        return word.getClassName();
    }

    public String getPeekSymbolClass(int i) {
        //peek 并不改变pos 只有get才会改变pos 注意 peek指针指在pos的下一个
        int peek = pos;
        peek += i;
        if (peek < wordList.size()) {
            return wordList.get(peek).getClassName();
        } else {
            return "EOF";
        }
    }

    public void addNonTermimal(String nonTermValue) {
        nonTermimal = new HashMap<>();
        nonTermimal.put("class","NONTER");
        nonTermimal.put("word",nonTermValue);
        wordAndGrammar.add(nonTermimal);
    }

    public Boolean isExpPrefix() {
        //ExpPrefix不含! 只有+ -
        return getWordClass().equals("IDENFR") || getWordClass().equals("PLUS") || getWordClass().equals("MINU")
                || getWordClass().equals("LPARENT") || getWordClass().equals("INTCON");
    }

    public Boolean isPrimaryExpPrefix() {
        return getWordClass().equals("IDENFR") || getWordClass().equals("LPARENT") || getWordClass().equals("INTCON");
    }

    public Boolean isFuncCallPrefix() {
        return getWordClass().equals("IDENFR") && getPeekSymbolClass(0).equals("LPARENT");
    }
    public boolean isDeclPrefix() {
        return (getWordClass().equals("CONSTTK") ||
                (getWordClass().equals("INTTK") && !getPeekSymbolClass(1).equals("LPARENT")));
    }

    public boolean isStmtPrefix() {
        return (getWordClass().equals("SEMICN") || getWordClass().equals("LBRACE") || getWordClass().equals("IFTK")
                || getWordClass().equals("WHILETK") || getWordClass().equals("BREAKTK") || getWordClass().equals("CONTINUETK")
                || getWordClass().equals("RETURNTK") || getWordClass().equals("PRINTFTK"))
                || isExpPrefix();
    }

    public boolean isBlockItemPrefix() {
        return isDeclPrefix() || isStmtPrefix();
    }

    public boolean isFuncDefPrefix() {
        return (getWordClass().equals("VOIDTK") ||
                (getWordClass().equals("INTTK") && !getPeekSymbolClass(0).equals("MAINTK")));
    }

    public boolean isFuncTypePrefix() {
        return getWordClass().equals("VOIDTK") || getWordClass().equals("INTTK");
    }

    public Boolean isUnaryOp() {
        //UnaryOp + - !
        return getWordClass().equals("PLUS") || getWordClass().equals("MINU") || getWordClass().equals("NOT");
    }

    public Boolean isMulOp() {
        return getWordClass().equals("MULT") || getWordClass().equals("DIV") || getWordClass().equals("MOD");
    }

    public Boolean isAddOp() {
        return getWordClass().equals("PLUS") || getWordClass().equals("MINU");
    }

    public Boolean isRelOp() {
        return getWordClass().equals("LSS") || getWordClass().equals("LEQ")
                || getWordClass().equals("GRE") || getWordClass().equals("GEQ");
    }

    public Boolean isEqOp() {
        return getWordClass().equals("EQL") || getWordClass().equals("NEQ");
    }

    public Boolean hasASSIGN() {
        int i = 0;
        while (!getPeekSymbolClass(i).equals("SEMICN") && !getPeekSymbolClass(i).equals("EOF")) {
            //一直往后看直到遇到;
            if (getPeekSymbolClass(i).equals("ASSIGN")) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void CompUnit() {
        while (isDeclPrefix()) {
            Decl();
        }
        while (isFuncDefPrefix()) {
            FuncDef();
        }
        MainFuncDef();
        addNonTermimal("<CompUnit>");
    }

    public void Decl() {
        if (getWordClass().equals("CONSTTK")) {
            ConstDecl();
        } else if (getWordClass().equals("INTTK")) {
            VarDecl();
        } else {
            //不是常数声明也不是变量声明
        }
    }

    public void BType() {
        if (getWordClass().equals("INTTK")) {
            getSymbol();
        } else {
            //不是int
        }
    }

    public void ConstDef() {
        if (getWordClass().equals("IDENFR")) {
            getSymbol();
            while (getWordClass().equals("LBRACK")) {
                getSymbol();
                ConstExp();
                if (getWordClass().equals("RBRACK")) {
                    getSymbol();
                } else {
                    //不以 ] 结尾
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getSymbol();
                ConstInitVal();
                addNonTermimal("<ConstDef>");
            } else {
                //不是 =
            }
        } else {
            //不是标识符 出错
        }
    }

    public void ConstDecl() {
        getSymbol();
        BType();
        ConstDef();
        while (getWordClass().equals("COMMA")) {
            getSymbol();
            ConstDef();
        }
        if (getWordClass().equals("SEMICN")) {
            getSymbol();
            addNonTermimal("<ConstDecl>");
        } else {
            //不以分号结尾
        }
    }

    public void ConstInitVal() {
        if (getWordClass().equals("LBRACE")) {
            getSymbol();
            if (getWordClass().equals("RBRACE")) {
                getSymbol();
                addNonTermimal("<ConstInitVal>");
            } else {
                ConstInitVal();
                while (getWordClass().equals("COMMA")) {
                    getSymbol();
                    ConstInitVal();
                }
                if (getWordClass().equals("RBRACE")) {
                    getSymbol();
                    addNonTermimal("<ConstInitVal>");
                } else {
                    //不是 }
                }
            }
        } else {
            ConstExp();
            addNonTermimal("<ConstInitVal>");
        }
    }

    public void VarDecl() {
        BType();
        VarDef();
        while (getWordClass().equals("COMMA")) {
            getSymbol();
            VarDef();
        }
        if (getWordClass().equals("SEMICN")) {
            getSymbol();
            addNonTermimal("<VarDecl>");
        } else {
            //不是;
        }
    }

    public void VarDef() {
        if (getWordClass().equals("IDENFR")) {
            getSymbol();
            while (getWordClass().equals("LBRACK")) {
                getSymbol();
                ConstExp();
                if (getWordClass().equals("RBRACK")) {
                    getSymbol();
                } else {
                    //不是]
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getSymbol();
                InitVal();
            }
            addNonTermimal("<VarDef>");
        } else {
            //不是 ident
        }
    }

    public void InitVal() {
        if (getWordClass().equals("LBRACE")) {
            getSymbol();
            if (getWordClass().equals("RBRACE")) {
                getSymbol();
                addNonTermimal("<InitVal>");
            } else {
                InitVal();
                while (getWordClass().equals("COMMA")) {
                    getSymbol();
                    InitVal();
                }
                if (getWordClass().equals("RBRACE")) {
                    getSymbol();
                    addNonTermimal("<InitVal>");
                } else {
                    //不是}
                }
            }
        } else if (isExpPrefix()) {
            Exp();
            addNonTermimal("<InitVal>");
        } else {
            //分支不对
        }
    }

    public void FuncDef() {
        FuncType();
        if (getWordClass().equals("IDENFR")) {
            getSymbol();
            if (getWordClass().equals("LPARENT")) {
                getSymbol();
                if (getWordClass().equals("RPARENT")) {
                    getSymbol();
                    Block();
                    addNonTermimal("<FuncDef>");
                } else {
                    FuncFParams();
                    if (getWordClass().equals("RPARENT")) {
                        getSymbol();
                        Block();
                        addNonTermimal("<FuncDef>");
                    } else {
                        //不是 )
                    }
                }
            } else {
                //不是 (
            }
        } else {
            //不是ident
        }
    }

    public void FuncType() {
        if (isFuncTypePrefix()) {
            getSymbol();
            addNonTermimal("<FuncType>");
        } else {
            //不是void int
        }
    }

    public void FuncFParams() {
        FuncFParam();
        while (getWordClass().equals("COMMA")) {
            getSymbol();
            FuncFParam();
        }
        addNonTermimal("<FuncFParams>");
    }

    public void FuncFParam() {
        BType();
        if (getWordClass().equals("IDENFR")) {
            getSymbol();
            if (getWordClass().equals("LBRACK")) {
                getSymbol();
                if (getWordClass().equals("RBRACK")) {
                    getSymbol();
                    while (getWordClass().equals("LBRACK")) {
                        getSymbol();
                        ConstExp();
                        if (getWordClass().equals("RBRACK")) {
                            getSymbol();
                        } else {
                            //不是 ]
                        }
                    }
                } else {
                    //不是 ]
                }
            }
            addNonTermimal("<FuncFParam>");
        } else {
            //不是ident
        }
    }

    public void Block() {
        if (getWordClass().equals("LBRACE")) {
            getSymbol();
            while (isBlockItemPrefix()) {
                BlockItem();
            }
            if (getWordClass().equals("RBRACE")) {
                getSymbol();
                addNonTermimal("<Block>");
            } else {
                //不是}
            }
        } else {
            //不是 {
        }
    }

    public void MainFuncDef() {
        if (getWordClass().equals("INTTK")) {
            getSymbol();
            if (getWordClass().equals("MAINTK")) {
                getSymbol();
                if (getWordClass().equals("LPARENT")) {
                    getSymbol();
                    if (getWordClass().equals("RPARENT")) {
                        getSymbol();
                        Block();
                        addNonTermimal("<MainFuncDef>");
                    } else {
                        //不是 )
                    }
                } else {
                    //不是(
                }
            } else {
                //不是main
            }
        } else {
            //不是int
        }
    }

    public void BlockItem() {
        if (isDeclPrefix()) {
            Decl();
        } else {
            Stmt();
        }
    }

    public void Stmt() {
        //一定不存在异常情况
        if (getWordClass().equals("IDENFR") && hasASSIGN()) {
            //LVal两种
            LVal();
            if (getWordClass().equals("ASSIGN")) {
                getSymbol();
                if (getWordClass().equals("GETINTTK")) {
                    getSymbol();
                    if (getWordClass().equals("LPARENT")) {
                        getSymbol();
                        if (getWordClass().equals("RPARENT")) {
                            getSymbol();
                            if (getWordClass().equals("SEMICN")) {
                                getSymbol();
                            } else {
                                //不是;
                            }
                        } else {
                            //不是)
                        }
                    } else {
                        //不是(
                    }
                } else if (isExpPrefix()) {
                    Exp();
                    if (getWordClass().equals("SEMICN")) {
                        getSymbol();
                    } else {
                        //不是;
                    }
                } else {
                    //不是getint Exp
                }
            } else {
                //不是=
            }
        } else if (isExpPrefix()) {
            //有Exp
            Exp();
            if (getWordClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是;
            }
        } else if (getWordClass().equals("SEMICN")) {
            //只有;没有Exp
            getSymbol();
        } else if (getWordClass().equals("LBRACE")) {
            Block();
        } else if (getWordClass().equals("IFTK")) {
            getSymbol();
            if (getWordClass().equals("LPARENT")) {
                getSymbol();
                Cond();
                if (getWordClass().equals("RPARENT")) {
                    getSymbol();
                    Stmt();
                    if (getWordClass().equals("ELSETK")) {
                        getSymbol();
                        Stmt();
                    }
                } else {
                    //不是)
                }
            } else {
                //不是(
            }
        } else if (getWordClass().equals("WHILETK")) {
            getSymbol();
            if (getWordClass().equals("LPARENT")) {
                getSymbol();
                Cond();
                if (getWordClass().equals("RPARENT")) {
                    getSymbol();
                    Stmt();
                } else {
                    //不是)
                }
            } else {
                //不是(
            }
        } else if (getWordClass().equals("BREAKTK")) {
            getSymbol();
            if (getWordClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是 ;
            }
        } else if (getWordClass().equals("CONTINUETK")) {
            getSymbol();
            if (getWordClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是 ;
            }
        } else if (getWordClass().equals("RETURNTK")) {
            getSymbol();
            if (isExpPrefix()) {
                Exp();
            }
            if (getWordClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是;
            }
        } else if (getWordClass().equals("PRINTFTK")) {
            getSymbol();
            if (getWordClass().equals("LPARENT")) {
                getSymbol();
                if (getWordClass().equals("STRCON")) {
                    getSymbol();
                    while (getWordClass().equals("COMMA")) {
                        getSymbol();
                        Exp();
                    }
                    if (getWordClass().equals("RPARENT")) {
                        getSymbol();
                        if (getWordClass().equals("SEMICN")) {
                            getSymbol();
                        } else {
                            //不是;
                        }
                    } else {
                        //不是)
                    }
                } else {
                    //不是字符串
                }
            } else {
                //不是(
            }
        } else {
            //不是分支!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        addNonTermimal("<Stmt>");
    }

    public void LVal() {
        if (getWordClass().equals("IDENFR")) {
            getSymbol();
            while (getWordClass().equals("LBRACK")) {
                getSymbol();
                Exp();
                if (getWordClass().equals("RBRACK")) {
                    getSymbol();
                } else {
                    //不是]
                }
            }
            addNonTermimal("<LVal>");
        } else {
            //不是Ident
        }
    }

    public void Cond() {
        LOrExp();
        addNonTermimal("<Cond>");
    }

    public void Exp() {
        AddExp();
        addNonTermimal("<Exp>");
    }

    public void AddExp() {
        MulExp();
        addNonTermimal("<AddExp>");
        while (isAddOp()) {
            getSymbol();
            MulExp();
            addNonTermimal("<AddExp>");
        }
    }

    public void PrimaryExp() {
        if (getWordClass().equals("LPARENT")) {
            getSymbol();
            Exp();
            if (getWordClass().equals("RPARENT")) {
                getSymbol();
                addNonTermimal("<PrimaryExp>");
            } else {
                //不是)
            }
        } else if (getWordClass().equals("IDENFR")) {
            LVal();
            addNonTermimal("<PrimaryExp>");
        } else if (getWordClass().equals("INTCON")) {
            Number();
            addNonTermimal("<PrimaryExp>");
        } else {
            //分支不对
        }
    }

    public void Number() {
        if (getWordClass().equals("INTCON")) {
            getSymbol();
            addNonTermimal("<Number>");
        } else {
            //不是数字
        }
    }

    public void UnaryExp() {
        if (isFuncCallPrefix()) {
            getSymbol();
            getSymbol();
            if (isExpPrefix()) {
                FuncRParams();
            }
            if (getWordClass().equals("RPARENT")) {
                getSymbol();
            } else {
                //不是)
            }
            addNonTermimal("<UnaryExp>");
        } else if (isPrimaryExpPrefix()) {
            PrimaryExp();
            addNonTermimal("<UnaryExp>");
        } else if (isUnaryOp()) {
            UnaryOp();
            UnaryExp();
            addNonTermimal("<UnaryExp>");
        } else {
            //不是分支
        }
    }

    public void UnaryOp() {
        if (isUnaryOp()) {
            getSymbol();
        } else {
            //不是 + - !
        }
        addNonTermimal("<UnaryOp>");
    }

    public void FuncRParams() {
        Exp();
        while (getWordClass().equals("COMMA")) {
            getSymbol();
            Exp();
        }
        addNonTermimal("<FuncRParams>");
    }

    public void MulExp() {
        UnaryExp();
        addNonTermimal("<MulExp>");
        while (isMulOp()) {
            getSymbol();
            UnaryExp();
            addNonTermimal("<MulExp>");
        }
    }

    public void RelExp() {
        AddExp();
        addNonTermimal("<RelExp>");
        while (isRelOp()) {
            getSymbol();
            AddExp();
            addNonTermimal("<RelExp>");
        }
    }

    public void EqExp() {
        RelExp();
        addNonTermimal("<EqExp>");
        while (isEqOp()) {
            getSymbol();
            RelExp();
            addNonTermimal("<EqExp>");
        }
    }

    public void LAndExp() {
        EqExp();
        addNonTermimal("<LAndExp>");
        while (getWordClass().equals("AND")) {
            getSymbol();
            EqExp();
            addNonTermimal("<LAndExp>");
        }
    }

    public void LOrExp() {
        LAndExp();
        addNonTermimal("<LOrExp>");
        while (getWordClass().equals("OR")) {
            getSymbol();
            LAndExp();
            addNonTermimal("<LOrExp>");
        }
    }

    public void ConstExp() {
        AddExp();
        addNonTermimal("<ConstExp>");
    }
}

