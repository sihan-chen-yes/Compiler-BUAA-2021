package GrammarAnalysis;

import ASTNode.*;
import ASTNode.Number;
import WordAnalysis.*;
import Enum.*;

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

    private Node ASTroot = null;

    private ErrorAnalysis errorAnalysis;

    public GrammarAnalysis(ArrayList<Word> wordList, File outputFile,ErrorAnalysis errorAnalysis) {
        this.wordList = wordList;
        try {
            this.writer = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.errorAnalysis = errorAnalysis;
    }

    public Node getASTroot() {
        return ASTroot;
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

    public boolean isFuncFParamsPrefix () {
        return getWordClass().equals("INTTK");
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

    public Boolean hasGETINTTK() {
        int i = 0;
        while (!getPeekSymbolClass(i).equals("SEMICN") && !getPeekSymbolClass(i).equals("EOF")) {
            //一直往后看直到遇到;
            if (getPeekSymbolClass(i).equals("GETINTTK")) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void CompUnit() {
        ASTroot = new CompUnit(pos - 1);
        while (isDeclPrefix()) {
            ASTroot.link(Decl());
        }
        while (isFuncDefPrefix()) {
            ASTroot.link(FuncDef());
        }
        ASTroot.link(MainFuncDef());
        addNonTermimal("<CompUnit>");
    }

    public Node Decl() {
        if (getWordClass().equals("CONSTTK")) {
            return ConstDecl();
        } else {
            return VarDecl();
        }
    }

    public Node BType() {
        BType node = null;
        if (getWordClass().equals("INTTK")) {
            getWord();
            node = new BType(pos - 1,DataType.INT);
        }
        return node;
    }

    public Node ConstDef() {
        ConstDef node = new ConstDef(word,pos - 1);
        int dim = 0;
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                dim++;
                getWord();
                node.link(ConstExp());
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRBRACK);
                    //不以 ] 结尾
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getWord();
                node.link(ConstInitVal());
            }
        }
        node.setDimType(dim);
        addNonTermimal("<ConstDef>");
        return node;
    }

    public Node ConstDecl() {
        ConstDecl node = new ConstDecl(pos - 1);
        getWord();//一定是const
        node.link(BType());
        node.link(ConstDef());
        while (getWordClass().equals("COMMA")) {
            getWord();
            node.link(ConstDef());
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不以分号结尾
        }
        addNonTermimal("<ConstDecl>");
        return node;
    }

    public Node ConstInitVal() {
        ConstInitVal node = new ConstInitVal(pos - 1);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            if (getWordClass().equals("RBRACE")) {
                getWord();
            } else {
                node.link(ConstInitVal());
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    node.link(ConstInitVal());
                }
                if (getWordClass().equals("RBRACE")) {
                    getWord();
                }
            }
        } else {
            node.link(ConstExp());
        }
        addNonTermimal("<ConstInitVal>");
        return node;
    }

    public Node VarDecl() {
        VarDecl node = new VarDecl(pos - 1);
        node.link(BType());
        node.link(VarDef());
        while (getWordClass().equals("COMMA")) {
            getWord();
            node.link(VarDef());
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不是;
        }
        addNonTermimal("<VarDecl>");
        return node;
    }

    public Node VarDef() {
        VarDef node = new VarDef(word,pos - 1);
        int dim = 0;
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                dim++;
                getWord();
                node.link(ConstExp());
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRBRACK);
                    //不是]
                }
            }
            if (getWordClass().equals("ASSIGN")) {
                getWord();
                node.link(InitVal());
            }
        }
        node.setDimType(dim);
        addNonTermimal("<VarDef>");
        return node;
    }

    public Node InitVal() {
        Node node = new InitVal(pos - 1);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            if (getWordClass().equals("RBRACE")) {
                getWord();
            } else {
                node.link(InitVal());
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    node.link(InitVal());
                }
                if (getWordClass().equals("RBRACE")) {
                    getWord();
                }
            }
        } else if (isExpPrefix()) {
            node.link(Exp());
        }
        addNonTermimal("<InitVal>");
        return node;
    }

    public Node FuncDef() {
        Node bType = FuncType();
        FuncDef node = new FuncDef(word,pos - 1);
        node.link(bType);
        if (getWordClass().equals("IDENFR")) {
            getWord();
            if (getWordClass().equals("LPARENT")) {
                getWord();
                if (isFuncFParamsPrefix()) {
                    node.link(FuncFParams());
                }
                if (getWordClass().equals("RPARENT")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                    //不是 )
                }
                node.link(Block());
                }
            }
        addNonTermimal("<FuncDef>");
        return node;
    }

    public Node FuncType() {
        FuncType node = null;
        if (getWordClass().equals("VOIDTK")) {
            getWord();
            node = new FuncType(pos - 1,DataType.VOID);
        } else {
            getWord();
            node = new FuncType(pos - 1,DataType.INT);
        }
        addNonTermimal("<FuncType>");
        return node;
    }

    public Node FuncFParams() {
        FuncFParams node = new FuncFParams(pos - 1);
        node.link(FuncFParam());
        while (getWordClass().equals("COMMA")) {
            getWord();
            node.link(FuncFParam());
        }
        addNonTermimal("<FuncFParams>");
        return node;
    }

    public Node FuncFParam() {
        Node btype = BType();
        FuncFParam node = new FuncFParam(word,pos - 1);
        node.link(btype);
        int dim = 0;
        if (getWordClass().equals("IDENFR")) {
            getWord();
            if (getWordClass().equals("LBRACK")) {
                dim++;
                getWord();
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRBRACK);
                    //不是 ]
                }
                while (getWordClass().equals("LBRACK")) {
                    dim++;
                    getWord();
                    node.link(ConstExp());
                    if (getWordClass().equals("RBRACK")) {
                        getWord();
                    } else {
                        errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRBRACK);
                        //不是 ]
                    }
                }
            }
        }
        node.setDimType(dim);
        addNonTermimal("<FuncFParam>");
        return node;
    }

    public Node Block() {
        Block node = new Block(pos - 1);
        if (getWordClass().equals("LBRACE")) {
            getWord();
            while (isBlockItemPrefix()) {
                node.link(BlockItem());
            }
            if (getWordClass().equals("RBRACE")) {
                node.setLastRBRACE(word);
                getWord();
            }
        }
        addNonTermimal("<Block>");
        return node;
    }

    public Node MainFuncDef() {
        MainFuncDef node = null;
        if (getWordClass().equals("INTTK")) {
            getWord();
            if (getWordClass().equals("MAINTK")) {
                node = new MainFuncDef(word,pos - 1);
                getWord();
                if (getWordClass().equals("LPARENT")) {
                    getWord();
                    if (getWordClass().equals("RPARENT")) {
                        getWord();
                    } else {
                        errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                        //不是 )
                    }
                    node.link(Block());
                }
            }
        }
        addNonTermimal("<MainFuncDef>");
        return node;
    }

    public Node BlockItem() {
        if (isDeclPrefix()) {
            return Decl();
        } else {
            return Stmt();
        }
    }
    public Node AssignStmt() {
        AssignStmt node = new AssignStmt(pos - 1);
        node.link(LVal());
        if (getWordClass().equals("ASSIGN")) {
            getWord();
            if (isExpPrefix()) {
                node.link(Exp());
            }
            if (getWordClass().equals("SEMICN")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
                //不是;
            }
        }
        return node;
    }

    public Node GetIntStmt() {
        GetIntStmt node = new GetIntStmt(pos - 1);
        node.link(LVal());
        if (getWordClass().equals("ASSIGN")) {
            getWord();
            if (getWordClass().equals("GETINTTK")) {
                getWord();
                if (getWordClass().equals("LPARENT")) {
                    getWord();
                    if (getWordClass().equals("RPARENT")) {
                        getWord();
                    } else {
                        errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                        //不是)
                    }
                }
            }
            if (getWordClass().equals("SEMICN")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
                //不是;
            }
        }
        return node;
    }

    public Node ExpStmt() {
        ExpStmt node = new ExpStmt(pos - 1);
        node.link(Exp());
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不是;
        }
        return node;
    }

    public Node EmptyStmt() {
        getWord();
        return new EmptyStmt(pos - 1);
    }

    public Node BlockStmt() {
        return Block();
    }

    public Node IfStmt() {
        IfStmt node = new IfStmt(pos - 1);
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            node.link(Cond());
            if (getWordClass().equals("RPARENT")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                //不是)
            }
            node.link(Stmt());
            if (getWordClass().equals("ELSETK")) {
                getWord();
                node.link(Stmt());
            }
        }
        return node;
    }

    public Node WhileStmt() {
        WhileStmt node = new WhileStmt(pos - 1);
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            node.link(Cond());
            if (getWordClass().equals("RPARENT")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                //不是)
            }
            node.link(Stmt());
        }
        return node;
    }

    public Node BreakStmt() {
        BreakStmt node = new BreakStmt(word,pos - 1);
        getWord();
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不是 ;
        }
        return node;
    }

    public Node ContinueStmt() {
        ContinueStmt node = new ContinueStmt(word,pos - 1);
        getWord();
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不是 ;
        }
        return node;
    }

    public Node ReturnStmt() {
        ReturnStmt node = new ReturnStmt(word,pos - 1);
        getWord();
        if (isExpPrefix()) {
            node.link(Exp());
        }
        if (getWordClass().equals("SEMICN")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
            //不是;
        }
        return node;
    }

    public Node PrintStmt() {
        PrintStmt node = new PrintStmt(word,pos - 1);
        getWord();
        if (getWordClass().equals("LPARENT")) {
            getWord();
            if (getWordClass().equals("STRCON")) {
                node.addFormatString(word);
                getWord();
                while (getWordClass().equals("COMMA")) {
                    getWord();
                    node.link(Exp());
                }
                if (getWordClass().equals("RPARENT")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                    //不是)
                }
                if (getWordClass().equals("SEMICN")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonSEMICN);
                    //不是;
                }
            }
        }
        return node;
    }

    public Node Stmt() {
        //一定不存在异常情况 子stmt只是逻辑判断的提取 不需要管curNode和输出
        Node node = null;
        if (getWordClass().equals("IDENFR") && hasASSIGN()) {
            //LVal两种
            if (hasGETINTTK()) {
                node = GetIntStmt();
            } else {
                node = AssignStmt();
            }
        } else if (isExpPrefix()) {
            //有Exp
            node = ExpStmt();
        } else if (getWordClass().equals("SEMICN")) {
            //只有;没有Exp
            node = EmptyStmt();
        } else if (getWordClass().equals("LBRACE")) {
            node = BlockStmt();
        } else if (getWordClass().equals("IFTK")) {
            node = IfStmt();
        } else if (getWordClass().equals("WHILETK")) {
            node = WhileStmt();
        } else if (getWordClass().equals("BREAKTK")) {
            node = BreakStmt();
        } else if (getWordClass().equals("CONTINUETK")) {
            node = ContinueStmt();
        } else if (getWordClass().equals("RETURNTK")) {
            node = ReturnStmt();
        } else if (getWordClass().equals("PRINTFTK")) {
            node = PrintStmt();
        }
        addNonTermimal("<Stmt>");
        return node;
    }

    public Node LVal() {
        LVal node = new LVal(word,pos - 1);
        if (getWordClass().equals("IDENFR")) {
            getWord();
            while (getWordClass().equals("LBRACK")) {
                getWord();
                node.link(Exp());
                if (getWordClass().equals("RBRACK")) {
                    getWord();
                } else {
                    errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRBRACK);
                    //不是]
                }
            }
        }
        addNonTermimal("<LVal>");
        return node;
    }

    public Node Cond() {
        Node node = new Cond(pos - 1);
        node.link(LOrExp());
        addNonTermimal("<Cond>");
        return node;
    }

    public Node Exp() {
        Exp node = new Exp(pos - 1);
        node.link(AddExp());
        addNonTermimal("<Exp>");
        return node;
    }

    public Node AddExp() {
        AddExp node = new AddExp(pos - 1);
        node.link(MulExp());
        addNonTermimal("<AddExp>");
        while (isAddOp()) {
            getWord();
            node.link(MulExp());
            addNonTermimal("<AddExp>");
        }
        return node;
    }

    public Node PrimaryExp() {
        Node node = null;
        if (getWordClass().equals("LPARENT")) {
            getWord();
            node = Exp();
            if (getWordClass().equals("RPARENT")) {
                getWord();
            } else {
                errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
                //不是)
            }
        } else if (getWordClass().equals("IDENFR")) {
            node = LVal();
        } else if (getWordClass().equals("INTCON")) {
            node = Number();
        }
        addNonTermimal("<PrimaryExp>");
        return node;
    }

    public Node Number() {
        Number node = new Number(word,pos - 1);
        if (getWordClass().equals("INTCON")) {
            getWord();
        }
        addNonTermimal("<Number>");
        return node;
    }

    public Node FuncCall() {
        Node node = new FuncCall(word,pos - 1);
        getWord();//函数名
        getWord();
        if (isExpPrefix()) {
            node.link(FuncRParams());
        }
        if (getWordClass().equals("RPARENT")) {
            getWord();
        } else {
            errorAnalysis.addError(lastWord.getLine(), ErrorType.nonRPARENT);
            //不是)
        }
        return node;
    }

    public Node UnaryExp() {
        Node node = new UnaryExp(pos - 1);
        if (isFuncCallPrefix()) {
            node.link(FuncCall());
        } else if (isPrimaryExpPrefix()) {
            node.link(PrimaryExp());
        } else if (isUnaryOp()) {
            UnaryOp();
            node = UnaryExp();
        }
        addNonTermimal("<UnaryExp>");
        return node;
    }

    public void UnaryOp() {
        if (isUnaryOp()) {
            getWord();
        }
        addNonTermimal("<UnaryOp>");
    }

    public Node FuncRParams() {
        Node node = new FuncRParams(pos - 1);
        Node funcRParam = new FuncRParam(pos - 1);
        funcRParam.link(Exp());
        node.link(funcRParam);
        while (getWordClass().equals("COMMA")) {
            getWord();
            funcRParam = new FuncRParam(pos - 1);
            funcRParam.link(Exp());
            node.link(funcRParam);
        }
        addNonTermimal("<FuncRParams>");
        return node;
    }

    public Node MulExp() {
        MulExp node = new MulExp(pos - 1);
        node.link(UnaryExp());
        addNonTermimal("<MulExp>");
        while (isMulOp()) {
            getWord();
            node.link(UnaryExp());
            addNonTermimal("<MulExp>");
        }
        return node;
    }

    public Node RelExp() {
        Node node = new RelExp(pos - 1);
        node.link(AddExp());
        addNonTermimal("<RelExp>");
        while (isRelOp()) {
            getWord();
            node.link(AddExp());
            addNonTermimal("<RelExp>");
        }
        return node;
    }

    public Node EqExp() {
        Node node = new EqExp(pos - 1);
        node.link(RelExp());
        addNonTermimal("<EqExp>");
        while (isEqOp()) {
            getWord();
            node.link(RelExp());
            addNonTermimal("<EqExp>");
        }
        return node;
    }

    public Node LAndExp() {
        Node node = new LAndExp(pos - 1);
        node.link(EqExp());
        addNonTermimal("<LAndExp>");
        while (getWordClass().equals("AND")) {
            getWord();
            node.link(EqExp());
            addNonTermimal("<LAndExp>");
        }
        return node;
    }

    public Node LOrExp() {
        Node node = new LOrExp(pos - 1);
        node.link(LAndExp());
        addNonTermimal("<LOrExp>");
        while (getWordClass().equals("OR")) {
            getWord();
            node.link(LAndExp());
            addNonTermimal("<LOrExp>");
        }
        return node;
    }

    public Node ConstExp() {
        ConstExp node = new ConstExp(pos - 1);
        node.link(AddExp());
        addNonTermimal("<ConstExp>");
        return node;
    }
}

