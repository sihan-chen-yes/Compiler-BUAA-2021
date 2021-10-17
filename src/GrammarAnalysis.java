import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GrammarAnalysis {
    private ArrayList<HashMap<String, String>> wordAndGrammar = new ArrayList<>();
    private Word word = null;
    private Word lastWord = null;
    private ArrayList<Word> wordList;
    private HashMap<String, String> nonTermimal = new HashMap<>();
    private int pos = 0;
    private FileWriter writer;
    private Node curNode = null;
    private ErrorAnalysis errorAnalysis;
    private ArrayList<Node> loopStmts = new ArrayList<>();

    public GrammarAnalysis(ArrayList<Word> wordList, File outputFile,ErrorAnalysis errorAnalysis) {
        this.wordList = wordList;
        try {
            this.writer = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.errorAnalysis = errorAnalysis;
    }

    public Node getCurNode() {
        return curNode;
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

    public void addNonLeaf(GrammarType type) {
        Node node = new Node(type,pos - 1);
        curNode.link(node);
        curNode = node;
    }

    public void addLeaf() {
        Node node = new Node(word,pos - 1);
        curNode.link(node);
    }

    public void backWard() {
        //非终结符需要backWard 终结符不需要backWard
        curNode = curNode.getFather();
    }

    public void recursionDown(){
        getWord();
        //进来之前symbol里面已经get到了新的word 出去的时候必须要getWord
        CompUnit();
    }

    public void getWord() {
        //刚开始的时候 symbol为null
        if (word != null && !word.getClassName().equals("EOF")) {
            HashMap symbol = new HashMap<>();
            symbol.put("class",word.getClassName());
            symbol.put("word",word.getWord());
            wordAndGrammar.add(symbol);
            addLeaf();
            lastWord = word;
        }
        if (pos < wordList.size()) {
            word = wordList.get(pos++);
        }
        else {
            word = new Word("EOF","",wordList.get(wordList.size() - 1).getLine());
        }
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
        curNode = new Node(GrammarType.CompUnit,pos);
        while (isDeclPrefix()) {
            Decl();
        }
        while (isFuncDefPrefix()) {
            FuncDef();
        }
        MainFuncDef();
        addNonTermimal("<CompUnit>");
        errorAnalysis.deliverInfo(loopStmts,curNode);
    }

    public void Decl() {
        addNonLeaf(GrammarType.Decl);
        if (getWordClass().equals("CONSTTK")) {
            ConstDecl();
        } else if (getWordClass().equals("INTTK")) {
            VarDecl();
        }
        backWard();
    }

    public void BType() {
        addNonLeaf(GrammarType.BType);
        if (getWordClass().equals("INTTK")) {
            getWord();
        }
        backWard();
    }

    public void ConstDef() {
        addNonLeaf(GrammarType.ConstDef);
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                getWord();
                ConstExp();
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRBRACK);
                    //不以 ] 结尾
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getWord();
                ConstInitVal();
                addNonTermimal("<ConstDef>");
            }
        }
        backWard();
    }

    public void ConstDecl() {
        addNonLeaf(GrammarType.ConstDecl);
        getWord();//一定是const
        BType();
        ConstDef();
        while (getWordClass().equals("COMMA")) {
            getWord();
            ConstDef();
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
            addNonTermimal("<ConstDecl>");
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不以分号结尾
        }
        backWard();
    }

    public void ConstInitVal() {
        addNonLeaf(GrammarType.ConstInitVal);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            if (getWordClass().equals("RBRACE")) {
                getWord();
                addNonTermimal("<ConstInitVal>");
            } else {
                ConstInitVal();
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    ConstInitVal();
                }
                if (getWordClass().equals("RBRACE")) {
                    getWord();
                    addNonTermimal("<ConstInitVal>");
                }
            }
        } else {
            ConstExp();
            addNonTermimal("<ConstInitVal>");
        }
        backWard();
    }

    public void VarDecl() {
        addNonLeaf(GrammarType.VarDecl);
        BType();
        VarDef();
        while (getWordClass().equals("COMMA")) {
            getWord();
            VarDef();
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
            addNonTermimal("<VarDecl>");
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不是;
        }
        backWard();
    }

    public void VarDef() {
        addNonLeaf(GrammarType.VarDef);
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                getWord();
                ConstExp();
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRBRACK);
                    //不是]
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getWord();
                InitVal();
            }
            addNonTermimal("<VarDef>");
        }
        backWard();
    }

    public void InitVal() {
        addNonLeaf(GrammarType.InitVal);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            if (getWordClass().equals("RBRACE")) {
                getWord();
                addNonTermimal("<InitVal>");
            } else {
                InitVal();
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    InitVal();
                }
                if (getWordClass().equals("RBRACE")) {
                    getWord();
                    addNonTermimal("<InitVal>");
                }
            }
        } else if (isExpPrefix()) {
            Exp();
            addNonTermimal("<InitVal>");
        }
        backWard();
    }

    public void FuncDef() {
        addNonLeaf(GrammarType.FuncDef);
        FuncType();
        if (getWordClass().equals("IDENFR")) {
            getWord();
            if (getWordClass().equals("LPARENT")) {
                getWord();
                if (getWordClass().equals("RPARENT")) {
                    getWord();
                    Block();
                    addNonTermimal("<FuncDef>");
                } else {
                    FuncFParams();
                    if (getWordClass().equals("RPARENT")) {
                        getWord();
                        Block();
                        addNonTermimal("<FuncDef>");
                    } else {
                        errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                        //不是 )
                    }
                }
            } else {
                errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                //不是 (
            }
        }
        backWard();
    }

    public void FuncType() {
        addNonLeaf(GrammarType.FuncType);
        if (isFuncTypePrefix()) {
            getWord();
            addNonTermimal("<FuncType>");
        }
        backWard();
    }

    public void FuncFParams() {
        addNonLeaf(GrammarType.FuncFParams);
        FuncFParam();
        while (getWordClass().equals("COMMA")) {
            getWord();
            FuncFParam();
        }
        addNonTermimal("<FuncFParams>");
        backWard();
    }

    public void FuncFParam() {
        addNonLeaf(GrammarType.FuncFParam);
        BType();
        if (getWordClass().equals("IDENFR")) {
            getWord();
            if (getWordClass().equals("LBRACK")) {
                getWord();
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                    while (getWordClass().equals("LBRACK")) {
                        getWord();
                        ConstExp();
                        if (getWordClass().equals("RBRACK")) {
                            getWord();
                        } else {
                            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRBRACK);
                            //不是 ]
                        }
                    }
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRBRACK);
                    //不是 ]
                }
            }
            addNonTermimal("<FuncFParam>");
        }
        backWard();
    }

    public void Block() {
        addNonLeaf(GrammarType.Block);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            while (isBlockItemPrefix()) {
                BlockItem();
            }
            if (getWordClass().equals("RBRACE")) {
                getWord();
                addNonTermimal("<Block>");
            }
        }
        backWard();
    }

    public void MainFuncDef() {
        addNonLeaf(GrammarType.MainFuncDef);
        if (getWordClass().equals("INTTK")) {
            getWord();
            if (getWordClass().equals("MAINTK")) {
                getWord();
                if (getWordClass().equals("LPARENT")) {
                    getWord();
                    if (getWordClass().equals("RPARENT")) {
                        getWord();
                        Block();
                        addNonTermimal("<MainFuncDef>");
                    } else {
                        errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                        //不是 )
                    }
                }
            }
        }
        backWard();
    }

    public void BlockItem() {
        addNonLeaf(GrammarType.BlockItem);
        if (isDeclPrefix()) {
            Decl();
        } else {
            Stmt();
        }
        backWard();
    }
    public void AssignStmt() {
        LVal();
        if (getWordClass().equals("ASSIGN")) {
            getWord();
            if (getWordClass().equals("GETINTTK")) {
                getWord();
                if (getWordClass().equals("LPARENT")) {
                    getWord();
                    if (getWordClass().equals("RPARENT")) {
                        getWord();
                        if (getWordClass().equals("SEMICN")) {
                            getWord();
                        } else {
                            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
                            //不是;
                        }
                    } else {
                        errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                        //不是)
                    }
                }
            } else if (isExpPrefix()) {
                Exp();
                if (getWordClass().equals("SEMICN")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
                    //不是;
                }
            }
        }
    }

    public void ExpStmt() {
        Exp();
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不是;
        }
    }

    public void EmptyStmt() {
        getWord();
    }

    public void BlockStmt() {
        Block();
    }

    public void IfStmt() {
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            Cond();
            if (getWordClass().equals("RPARENT")) {
                getWord();
                Stmt();
                if (getWordClass().equals("ELSETK")) {
                    getWord();
                    Stmt();
                }
            } else {
                errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                //不是)
            }
        }
    }

    public void WhileStmt() {
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            Cond();
            if (getWordClass().equals("RPARENT")) {
                getWord();
                Stmt();
            } else {
                errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                //不是)
            }
        }
    }

    public void BreakStmt() {
        getWord();
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不是 ;
        }
    }

    public void ContinueStmt() {
        getWord();
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不是 ;
        }
    }

    public void ReturnStmt() {
        getWord();
        if (isExpPrefix()) {
            Exp();
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
            //不是;
        }
    }

    public void PrintStmt() {
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            if (getWordClass().equals("STRCON")) {
                getWord();
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    Exp();
                }
                if (getWordClass().equals("RPARENT")) {
                    getWord();
                    if (getWordClass().equals("SEMICN")) {
                        getWord();
                    } else {
                        errorAnalysis.addError(lastWord.getLine(),ErrorType.nonSEMICN);
                        //不是;
                    }
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                    //不是)
                }
            }
        }
    }

    public void Stmt() {
        //一定不存在异常情况 子stmt只是逻辑判断的提取 不需要管curNode和输出
        if (getWordClass().equals("IDENFR") && hasASSIGN()) {
            //LVal两种
            addNonLeaf(GrammarType.AssignStmt);
            AssignStmt();
        } else if (isExpPrefix()) {
            //有Exp
            addNonLeaf(GrammarType.ExpStmt);
            ExpStmt();
        } else if (getWordClass().equals("SEMICN")) {
            //只有;没有Exp
            addNonLeaf(GrammarType.EmptyStmt);
            EmptyStmt();
        } else if (getWordClass().equals("LBRACE")) {
            addNonLeaf(GrammarType.BlockStmt);
            BlockStmt();
        } else if (getWordClass().equals("IFTK")) {
            addNonLeaf(GrammarType.IfStmt);
            IfStmt();
        } else if (getWordClass().equals("WHILETK")) {
            addNonLeaf(GrammarType.WhileStmt);
            WhileStmt();
        } else if (getWordClass().equals("BREAKTK")) {
            addNonLeaf(GrammarType.BreakStmt);
            loopStmts.add(curNode);
            BreakStmt();
        } else if (getWordClass().equals("CONTINUETK")) {
            addNonLeaf(GrammarType.ContinueStmt);
            loopStmts.add(curNode);
            ContinueStmt();
        } else if (getWordClass().equals("RETURNTK")) {
            addNonLeaf(GrammarType.ReturnStmt);
            ReturnStmt();
        } else if (getWordClass().equals("PRINTFTK")) {
            addNonLeaf(GrammarType.PrintStmt);
            PrintStmt();
        }
        addNonTermimal("<Stmt>");
        backWard();
    }

    public void LVal() {
        addNonLeaf(GrammarType.LVal);
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                getWord();
                Exp();
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRBRACK);
                    //不是]
                }
            }
            addNonTermimal("<LVal>");
        }
        backWard();
    }

    public void Cond() {
        addNonLeaf(GrammarType.Cond);
        LOrExp();
        addNonTermimal("<Cond>");
        backWard();
    }

    public void Exp() {
        addNonLeaf(GrammarType.Exp);
        AddExp();
        addNonTermimal("<Exp>");
        backWard();
    }

    public void AddExp() {
        addNonLeaf(GrammarType.AddExp);
        MulExp();
        addNonTermimal("<AddExp>");
        while (isAddOp()) {
            getWord();
            MulExp();
            addNonTermimal("<AddExp>");
        }
        backWard();
    }

    public void PrimaryExp() {
        addNonLeaf(GrammarType.PrimaryExp);
        if (getWordClass().equals("LPARENT")) {
            getWord();
            Exp();
            if (getWordClass().equals("RPARENT")) {
                getWord();
                addNonTermimal("<PrimaryExp>");
            } else {
                errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
                //不是)
            }
        } else if (getWordClass().equals("IDENFR")) {
            LVal();
            addNonTermimal("<PrimaryExp>");
        } else if (getWordClass().equals("INTCON")) {
            Number();
            addNonTermimal("<PrimaryExp>");
        }
        backWard();
    }

    public void Number() {
        addNonLeaf(GrammarType.Number);
        if (getWordClass().equals("INTCON")) {
            getWord();
            addNonTermimal("<Number>");
        }
        backWard();
    }

    public void UnaryExp() {
        addNonLeaf(GrammarType.UnaryExp);
        if (isFuncCallPrefix()) {
            getWord();
            getWord();
            if (isExpPrefix()) {
                FuncRParams();
            }
            if (getWordClass().equals("RPARENT")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(),ErrorType.nonRPARENT);
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
        }
        backWard();
    }

    public void UnaryOp() {
        addNonLeaf(GrammarType.UnaryOp);
        if (isUnaryOp()) {
            getWord();
        }
        addNonTermimal("<UnaryOp>");
        backWard();
    }

    public void FuncRParams() {
        addNonLeaf(GrammarType.FuncRParams);
        Exp();
        while (getWordClass().equals("COMMA")) {
            getWord();
            Exp();
        }
        addNonTermimal("<FuncRParams>");
        backWard();
    }

    public void MulExp() {
        addNonLeaf(GrammarType.MulExp);
        UnaryExp();
        addNonTermimal("<MulExp>");
        while (isMulOp()) {
            getWord();
            UnaryExp();
            addNonTermimal("<MulExp>");
        }
        backWard();
    }

    public void RelExp() {
        addNonLeaf(GrammarType.RelExp);
        AddExp();
        addNonTermimal("<RelExp>");
        while (isRelOp()) {
            getWord();
            AddExp();
            addNonTermimal("<RelExp>");
        }
        backWard();
    }

    public void EqExp() {
        addNonLeaf(GrammarType.EqExp);
        RelExp();
        addNonTermimal("<EqExp>");
        while (isEqOp()) {
            getWord();
            RelExp();
            addNonTermimal("<EqExp>");
        }
        backWard();
    }

    public void LAndExp() {
        addNonLeaf(GrammarType.LAndExp);
        EqExp();
        addNonTermimal("<LAndExp>");
        while (getWordClass().equals("AND")) {
            getWord();
            EqExp();
            addNonTermimal("<LAndExp>");
        }
        backWard();
    }

    public void LOrExp() {
        addNonLeaf(GrammarType.LOrExp);
        LAndExp();
        addNonTermimal("<LOrExp>");
        while (getWordClass().equals("OR")) {
            getWord();
            LAndExp();
            addNonTermimal("<LOrExp>");
        }
        backWard();
    }

    public void ConstExp() {
        addNonLeaf(GrammarType.ConstExp);
        AddExp();
        addNonTermimal("<ConstExp>");
        backWard();
    }
}

