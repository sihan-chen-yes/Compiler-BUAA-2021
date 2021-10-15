import java.util.ArrayList;
import java.util.HashMap;

public class GrammarAnalysis {
    private ArrayList<HashMap<String, String>> wordAndGrammar = new ArrayList<>();
    private HashMap<String, String> symbol = null;
    private HashMap<String, String> peekSymbol = new HashMap<>();
    private WordAnalysis wordAnalysis;
    private HashMap<String, String> nonTermimal = new HashMap<>();

    public GrammarAnalysis(WordAnalysis wordAnalysis) {
        this.wordAnalysis = wordAnalysis;
    }

    public ArrayList<HashMap<String, String>> recursionDown(){
        getSymbol();
        //进来之前symbol里面已经get到了新的symbol 出去的时候必须要getSymbol
        CompUnit();
        return wordAndGrammar;
    }

    public void getSymbol() {
        if (symbol != null) {
            wordAndGrammar.add(symbol);
        }
        //刚开始的时候 symbol为null
        do {
            symbol = wordAnalysis.getSymbol(false);
        } while (symbol.get("class").equals("ANNO"));
        //过滤注释
    }

    public String getSymbolClass() {
        return symbol.get("class");
    }

    public String getPeekSymbolClass(int i) {
        //peek 并不改变指针 只有get才会改变指针 注意 peek指针指在当前指针的下一个
        peekSymbol = wordAnalysis.peekSymbol(i);
        return peekSymbol.get("class");
    }

    public void addNonTermimal(String nonTermValue) {
        nonTermimal = new HashMap<>();
        nonTermimal.put("class","NONTER");
        nonTermimal.put("word",nonTermValue);
        wordAndGrammar.add(nonTermimal);
    }

    public Boolean isExpPrefix() {
        //ExpPrefix不含! 只有+ -
        return getSymbolClass().equals("IDENFR") || getSymbolClass().equals("PLUS") || getSymbolClass().equals("MINU")
                || getSymbolClass().equals("LPARENT") || getSymbolClass().equals("INTCON");
    }

    public Boolean isPrimaryExpPrefix() {
        return getSymbolClass().equals("IDENFR") || getSymbolClass().equals("LPARENT") || getSymbolClass().equals("INTCON");
    }

    public Boolean isFuncCallPrefix() {
        return getSymbolClass().equals("IDENFR") && getPeekSymbolClass(0).equals("LPARENT");
    }
    public boolean isDeclPrefix() {
        return (getSymbolClass().equals("CONSTTK") ||
                (getSymbolClass().equals("INTTK") && !getPeekSymbolClass(1).equals("LPARENT")));
    }

    public boolean isStmtPrefix() {
        return (getSymbolClass().equals("SEMICN") || getSymbolClass().equals("LBRACE") || getSymbolClass().equals("IFTK")
                || getSymbolClass().equals("WHILETK") || getSymbolClass().equals("BREAKTK") || getSymbolClass().equals("CONTINUETK")
                || getSymbolClass().equals("RETURNTK") || getSymbolClass().equals("PRINTFTK"))
                || isExpPrefix();
    }

    public boolean isBlockItemPrefix() {
        return isDeclPrefix() || isStmtPrefix();
    }

    public boolean isFuncDefPrefix() {
        return (getSymbolClass().equals("VOIDTK") ||
                (getSymbolClass().equals("INTTK") && !getPeekSymbolClass(0).equals("MAINTK")));
    }

    public boolean isFuncTypePrefix() {
        return getSymbolClass().equals("VOIDTK") || getSymbolClass().equals("INTTK");
    }

    public Boolean isUnaryOp() {
        //UnaryOp + - !
        return getSymbolClass().equals("PLUS") || getSymbolClass().equals("MINU") || getSymbolClass().equals("NOT");
    }

    public Boolean isMulOp() {
        return getSymbolClass().equals("MULT") || getSymbolClass().equals("DIV") || getSymbolClass().equals("MOD");
    }

    public Boolean isAddOp() {
        return getSymbolClass().equals("PLUS") || getSymbolClass().equals("MINU");
    }

    public Boolean isRelOp() {
        return getSymbolClass().equals("LSS") || getSymbolClass().equals("LEQ")
                || getSymbolClass().equals("GRE") || getSymbolClass().equals("GEQ");
    }

    public Boolean isEqOp() {
        return getSymbolClass().equals("EQL") || getSymbolClass().equals("NEQ");
    }

    public Boolean hasASSIGN() {
        int i = 0;
        while (!getPeekSymbolClass(i).equals("SEMICN")) {
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
        if (getSymbolClass().equals("CONSTTK")) {
            ConstDecl();
        } else if (getSymbolClass().equals("INTTK")) {
            VarDecl();
        } else {
            //不是常数声明也不是变量声明
        }
    }

    public void BType() {
        if (getSymbolClass().equals("INTTK")) {
            getSymbol();
        } else {
            //不是int
        }
    }

    public void ConstDef() {
        if (getSymbolClass().equals("IDENFR")) {
            getSymbol();
            while (getSymbolClass().equals("LBRACK")) {
                getSymbol();
                ConstExp();
                if (getSymbolClass().equals("RBRACK")) {
                    getSymbol();
                } else {
                    //不以 ] 结尾
                }
            }
            if (getSymbolClass().equals("ASSIGN")) {
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
        while (getSymbolClass().equals("COMMA")) {
            getSymbol();
            ConstDef();
        }
        if (getSymbolClass().equals("SEMICN")) {
            getSymbol();
            addNonTermimal("<ConstDecl>");
        } else {
            //不以分号结尾
        }
    }

    public void ConstInitVal() {
        if (getSymbolClass().equals("LBRACE")) {
            getSymbol();
            if (getSymbolClass().equals("RBRACE")) {
                getSymbol();
                addNonTermimal("<ConstInitVal>");
            } else {
                ConstInitVal();
                while (getSymbolClass().equals("COMMA")) {
                    getSymbol();
                    ConstInitVal();
                }
                if (getSymbolClass().equals("RBRACE")) {
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
        while (getSymbolClass().equals("COMMA")) {
            getSymbol();
            VarDef();
        }
        if (getSymbolClass().equals("SEMICN")) {
            getSymbol();
            addNonTermimal("<VarDecl>");
        } else {
            //不是;
        }
    }

    public void VarDef() {
        if (getSymbolClass().equals("IDENFR")) {
            getSymbol();
            while (getSymbolClass().equals("LBRACK")) {
                getSymbol();
                ConstExp();
                if (getSymbolClass().equals("RBRACK")) {
                    getSymbol();
                } else {
                    //不是]
                }
            }
            if (getSymbolClass().equals("ASSIGN")) {
                getSymbol();
                InitVal();
            }
            addNonTermimal("<VarDef>");
        } else {
            //不是 ident
        }
    }

    public void InitVal() {
        if (getSymbolClass().equals("LBRACE")) {
            getSymbol();
            if (getSymbolClass().equals("RBRACE")) {
                getSymbol();
                addNonTermimal("<InitVal>");
            } else {
                InitVal();
                while (getSymbolClass().equals("COMMA")) {
                    getSymbol();
                    InitVal();
                }
                if (getSymbolClass().equals("RBRACE")) {
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
        if (getSymbolClass().equals("IDENFR")) {
            getSymbol();
            if (getSymbolClass().equals("LPARENT")) {
                getSymbol();
                if (getSymbolClass().equals("RPARENT")) {
                    getSymbol();
                    Block();
                    addNonTermimal("<FuncDef>");
                } else {
                    FuncFParams();
                    if (getSymbolClass().equals("RPARENT")) {
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
        while (getSymbolClass().equals("COMMA")) {
            getSymbol();
            FuncFParam();
        }
        addNonTermimal("<FuncFParams>");
    }

    public void FuncFParam() {
        BType();
        if (getSymbolClass().equals("IDENFR")) {
            getSymbol();
            if (getSymbolClass().equals("LBRACK")) {
                getSymbol();
                if (getSymbolClass().equals("RBRACK")) {
                    getSymbol();
                    while (getSymbolClass().equals("LBRACK")) {
                        getSymbol();
                        ConstExp();
                        if (getSymbolClass().equals("RBRACK")) {
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
        if (getSymbolClass().equals("LBRACE")) {
            getSymbol();
            while (isBlockItemPrefix()) {
                BlockItem();
            }
            if (getSymbolClass().equals("RBRACE")) {
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
        if (getSymbolClass().equals("INTTK")) {
            getSymbol();
            if (getSymbolClass().equals("MAINTK")) {
                getSymbol();
                if (getSymbolClass().equals("LPARENT")) {
                    getSymbol();
                    if (getSymbolClass().equals("RPARENT")) {
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
        if (getSymbolClass().equals("IDENFR") && hasASSIGN()) {
            //LVal两种
            LVal();
            if (getSymbolClass().equals("ASSIGN")) {
                getSymbol();
                if (getSymbolClass().equals("GETINTTK")) {
                    getSymbol();
                    if (getSymbolClass().equals("LPARENT")) {
                        getSymbol();
                        if (getSymbolClass().equals("RPARENT")) {
                            getSymbol();
                            if (getSymbolClass().equals("SEMICN")) {
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
                    if (getSymbolClass().equals("SEMICN")) {
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
            if (getSymbolClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是;
            }
        } else if (getSymbolClass().equals("SEMICN")) {
            //只有;没有Exp
            getSymbol();
        } else if (getSymbolClass().equals("LBRACE")) {
            Block();
        } else if (getSymbolClass().equals("IFTK")) {
            getSymbol();
            if (getSymbolClass().equals("LPARENT")) {
                getSymbol();
                Cond();
                if (getSymbolClass().equals("RPARENT")) {
                    getSymbol();
                    Stmt();
                    if (getSymbolClass().equals("ELSETK")) {
                        getSymbol();
                        Stmt();
                    }
                } else {
                    //不是)
                }
            } else {
                //不是(
            }
        } else if (getSymbolClass().equals("WHILETK")) {
            getSymbol();
            if (getSymbolClass().equals("LPARENT")) {
                getSymbol();
                Cond();
                if (getSymbolClass().equals("RPARENT")) {
                    getSymbol();
                    Stmt();
                } else {
                    //不是)
                }
            } else {
                //不是(
            }
        } else if (getSymbolClass().equals("BREAKTK")) {
            getSymbol();
            if (getSymbolClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是 ;
            }
        } else if (getSymbolClass().equals("CONTINUETK")) {
            getSymbol();
            if (getSymbolClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是 ;
            }
        } else if (getSymbolClass().equals("RETURNTK")) {
            getSymbol();
            if (isExpPrefix()) {
                Exp();
            }
            if (getSymbolClass().equals("SEMICN")) {
                getSymbol();
            } else {
                //不是;
            }
        } else if (getSymbolClass().equals("PRINTFTK")) {
            getSymbol();
            if (getSymbolClass().equals("LPARENT")) {
                getSymbol();
                if (getSymbolClass().equals("STRCON")) {
                    getSymbol();
                    while (getSymbolClass().equals("COMMA")) {
                        getSymbol();
                        Exp();
                    }
                    if (getSymbolClass().equals("RPARENT")) {
                        getSymbol();
                        if (getSymbolClass().equals("SEMICN")) {
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
        if (getSymbolClass().equals("IDENFR")) {
            getSymbol();
            while (getSymbolClass().equals("LBRACK")) {
                getSymbol();
                Exp();
                if (getSymbolClass().equals("RBRACK")) {
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
        if (getSymbolClass().equals("LPARENT")) {
            getSymbol();
            Exp();
            if (getSymbolClass().equals("RPARENT")) {
                getSymbol();
                addNonTermimal("<PrimaryExp>");
            } else {
                //不是)
            }
        } else if (getSymbolClass().equals("IDENFR")) {
            LVal();
            addNonTermimal("<PrimaryExp>");
        } else if (getSymbolClass().equals("INTCON")) {
            Number();
            addNonTermimal("<PrimaryExp>");
        } else {
            //分支不对
        }
    }

    public void Number() {
        if (getSymbolClass().equals("INTCON")) {
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
            if (getSymbolClass().equals("RPARENT")) {
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
        while (getSymbolClass().equals("COMMA")) {
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
        while (getSymbolClass().equals("AND")) {
            getSymbol();
            EqExp();
            addNonTermimal("<LAndExp>");
        }
    }

    public void LOrExp() {
        LAndExp();
        addNonTermimal("<LOrExp>");
        while (getSymbolClass().equals("OR")) {
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

