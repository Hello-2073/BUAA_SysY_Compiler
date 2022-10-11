package compiler;

import compiler.error.Error;
import compiler.error.ErrorRecorder;
import compiler.syntax.*;
import compiler.syntax.cond.*;
import compiler.syntax.decl.*;
import compiler.syntax.decl.var.*;
import compiler.syntax.exp.*;
import compiler.syntax.exp.Number;
import compiler.syntax.decl.func.*;
import compiler.syntax.exp.unaryExp.FuncCallUnaryExp;
import compiler.syntax.exp.unaryExp.PrimaryUnaryExp;
import compiler.syntax.exp.unaryExp.UnaryExp;
import compiler.syntax.stmt.*;
import compiler.type.SyntaxType;

import static compiler.type.SyntaxType.*;

public class Parser {
    private final Tokens tokens;

    public Parser(Tokens tokens) {
        this.tokens = tokens;
    }

    public Nonterminal parse() {
        return compileUnit();
    }

    private Terminal getTerminal() {
        Terminal terminal = tokens.get();
        //System.out.println(terminal.getContent());
        return terminal;
    }

    private void mustGetTerminal(Nonterminal nonterminal, SyntaxType type, String error) {
        try {
            nonterminal.addChild(tokens.tryGet(type));
        } catch (Exception e) {
            nonterminal.addChild(new Terminal(type, "", tokens.getRow(-1)));
            System.out.println("第 " + tokens.getRow(-1) + " 行：缺少 " + type + "。");
            if (error != null) {
                ErrorRecorder.insert(new Error(tokens.getRow(-1), error));
            }
        }
    }

    private Nonterminal compileUnit() {
        /*
            CompUnit → {Decl} {FuncDef} MainFuncDef
                0(Decl):        {const, int},  1(Decl):        {int, Ident},  2(Decl):        {Ident, =, ;}
                0(FuncDef):     {int, void},   1(Decl):        {Ident},       2(FuncDef):     {(}
                0(MainFuncDef): {int},         1(MainFuncDef): {main},        2(MainFuncDef): {(}
         */
        CompileUnit compileUnit = new CompileUnit();
        while (tokens.hasNext()) {
            SyntaxType type = tokens.getType(0);
            if (type == CONSTTK) {
                compileUnit.addChild(decl());
            } else if (type == VOIDTK) {
                compileUnit.addChild(funcDef());
            } else if (tokens.getType(1) == MAINTK) {
                compileUnit.addChild(mainFuncDef());
            } else if (tokens.getType(2) == LPARENT) {
                compileUnit.addChild(funcDef());
            } else {
                compileUnit.addChild(decl());
            }
        }
        return compileUnit;
    }

    private Decl decl() {
        /* Rule: Decl → ConstDecl | VarDecl
         *
         *  0(ConstDecl): {const}
         *  0(VarDecl):   {int}
         */
        Decl decl = new Decl();
        if(tokens.getType(0) == CONSTTK) {
            decl.addChild(constDecl());
        } else {
            decl.addChild(varDecl());
        }
        return decl;
    }

    private ConstDecl constDecl() {
        /* Rule: ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' */
        ConstDecl constDecl = new ConstDecl();
        mustGetTerminal(constDecl, CONSTTK, "");
        constDecl.addChild(bType());
        constDecl.addChild(constDef());
        while (tokens.getType(0) == COMMA) {
            constDecl.addChild(getTerminal());
            constDecl.addChild(constDef());
        }
        mustGetTerminal(constDecl, SEMICN, "i");
        return constDecl;
    }

    private BType bType() {
        /* Rule: BType → 'int'
         */
        BType bType = new BType();
        mustGetTerminal(bType, INTTK, "");
        return bType;
    }

    private ConstDef constDef() {
        /* Rule: ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
         */
        ConstDef constDef = new ConstDef();
        mustGetTerminal(constDef, IDENFR, "");
        while (tokens.getType(0) == SyntaxType.LBRACK) {
            constDef.addChild(getTerminal());
            constDef.addChild(constExp());
            mustGetTerminal(constDef, RBRACK, "k");
        }
        mustGetTerminal(constDef, ASSIGN, "");
        constDef.addChild(constInitVal());
        return constDef;
    }

    private ConstInitVal constInitVal() {
        /* Rule: ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}' */
        ConstInitVal constInitVal = new ConstInitVal();
        if (tokens.getType(0) == LBRACE) {
            constInitVal.addChild(getTerminal());
            if (tokens.getType(0) != RBRACE) {
                constInitVal.addChild(constInitVal());
                while (tokens.getType(0) == COMMA) {
                    constInitVal.addChild(getTerminal());
                    constInitVal.addChild(constInitVal());
                }
            }
            mustGetTerminal(constInitVal, RBRACE, "");
        } else {
            constInitVal.addChild(constExp());
        }
        return constInitVal;
    }

    private VarDecl varDecl() {
        /* Rule: VarDecl → BType VarDef { ',' VarDef } ';' */
        VarDecl varDecl = new VarDecl();
        varDecl.addChild(bType());
        varDecl.addChild(varDef());
        while (tokens.getType(0) == COMMA) {
            varDecl.addChild(getTerminal());
            varDecl.addChild(varDef());
        }
        mustGetTerminal(varDecl, SEMICN, "i");
        return varDecl;
    }

    private VarDef varDef() {
        /* Rule: VarDef → Ident { '[' ConstExp ']' } [ '=' InitVal ] */
        VarDef varDef = new VarDef();
        mustGetTerminal(varDef, IDENFR, "");
        while (tokens.getType(0) == SyntaxType.LBRACK) {
            varDef.addChild(getTerminal());
            varDef.addChild(constExp());
            mustGetTerminal(varDef, RBRACK, "k");
        }
        if (tokens.getType(0) == ASSIGN) {
            varDef.addChild(getTerminal());
            varDef.addChild(initVal());
        }
        return varDef;
    }

    private InitVal initVal() {
        /* Rule: InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}' */
        InitVal initVal = new InitVal();
        if (tokens.getType(0) == SyntaxType.LBRACE) {
            initVal.addChild(getTerminal());
            if (tokens.getType(0) != RBRACE) {
                initVal.addChild(initVal());
                while (tokens.getType(0) == COMMA) {
                    initVal.addChild(getTerminal());
                    initVal.addChild(initVal());
                }
            }
            mustGetTerminal(initVal, RBRACE, "");
        } else {
            initVal.addChild(exp());
        }
        return initVal;
    }

    private FuncDef funcDef() {
        /* Rule: FuncDef → FuncType Ident '(' [FuncFParams] ')' Block */
        FuncDef funcDef = new FuncDef();
        funcDef.addChild(funcType());
        mustGetTerminal(funcDef, IDENFR, null);
        mustGetTerminal(funcDef, LPARENT, null);
        if (tokens.getType(0) == INTTK) {
            funcDef.addChild(funcFParams());
        }
        mustGetTerminal(funcDef, RPARENT, "j");
        funcDef.addChild(block());
        return funcDef;
    }

    private MainFuncDef mainFuncDef() {
        /* MainFuncDef → 'int' 'main' '(' ')' Block */
        MainFuncDef mainFuncDef = new MainFuncDef();
        mustGetTerminal(mainFuncDef, INTTK, "");
        mustGetTerminal(mainFuncDef, MAINTK, "");
        mustGetTerminal(mainFuncDef, LPARENT, "");
        mustGetTerminal(mainFuncDef, RPARENT, "j");
        mainFuncDef.addChild(block());
        return mainFuncDef;
    }

    private FuncType funcType() {
        /* FuncType → 'void' | 'int' */
        FuncType funcType = new FuncType();
        SyntaxType type = tokens.getType(0);
        if (type == VOIDTK || type == INTTK) {
            funcType.addChild(getTerminal());
        } else {
            /* TODO : 未知返回类型 */
            System.out.println("第 " + tokens.getRow(0) + " 行： 未知返回值类型" + type);
        }
        return funcType;
    }

    private FuncFParams funcFParams() {
        /* FuncFParams → FuncFParam { ',' FuncFParam } */
        FuncFParams funcFParams = new FuncFParams();
        funcFParams.addChild(funcFParam());
        while (tokens.getType(0) == COMMA) {
            funcFParams.addChild(tokens.get());
            funcFParams.addChild(funcFParam());
        }
        return funcFParams;
    }

    private FuncFParam funcFParam() {
        /* FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }] */
        FuncFParam funcFParam = new FuncFParam();
        funcFParam.addChild(bType());
        mustGetTerminal(funcFParam, IDENFR, "");
        if (tokens.getType(0) == LBRACK) {
            funcFParam.addChild(getTerminal());
            mustGetTerminal(funcFParam, RBRACK, "k");
            while (tokens.getType(0) == LBRACK) {
                funcFParam.addChild(getTerminal());
                funcFParam.addChild(constExp());
                mustGetTerminal(funcFParam, RBRACK, "k");
            }
        }
        return funcFParam;
    }

    private Block block() {
        /* Block → '{' { BlockItem } '}' */
        Block block = new Block();
        mustGetTerminal(block, LBRACE, "");
        while (tokens.getType(0) != RBRACE) {
            block.addChild(blockItem());
        }
        mustGetTerminal(block, RBRACE, "");
        return block;
    }

    private BlockItem blockItem() {
        /* BlockItem → Decl | Stmt */
        BlockItem blockItem = new BlockItem();
        SyntaxType type = tokens.getType(0);
        if (type == CONSTTK || type == INTTK) {
            blockItem.addChild(decl());
        } else {
            blockItem.addChild(stmt());
        }
        return blockItem;
    }

    private Stmt stmt() {
        /* Stmt → LVal '=' Exp ';'
           | [Exp] ';'
           | Block
           | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
           | 'while' '(' Cond ')' Stmt
           | 'break' ';'
           | 'continue' ';'
           | 'return' [Exp] ';'
           | LVal '=' 'getint''('')'';'
           | 'printf''('FormatString{','Exp}')'';'
         */
        switch (tokens.getType(0)) {
            case LBRACE:
                Stmt stmt = new BlockStmt();
                stmt.addChild(block());
                return stmt;
            case IDENFR:
                return lValStmt();
            case SEMICN:
            case INTCON:
            case LPARENT:
            case PLUS:
            case MINU:
            case NOT:
                return expStmt();
            case IFTK:
            case WHILETK:
                return ifOrWhileStmt();
            case BREAKTK:
            case CONTINUETK:
                return breakOrContinueStmt();
            case RETURNTK:
                return returnStmt();
            case PRINTFTK:
                return printfStmt();
            default:
                throw new RuntimeException(tokens.getType(0).toString());
        }
    }

    private Stmt expStmt() {
        Stmt stmt = new ExpStmt();
        if (tokens.getType(0) != SEMICN) {
            stmt.addChild(exp());
        }
        mustGetTerminal(stmt, SEMICN, "i");
        return stmt;
    }

    private Stmt breakOrContinueStmt() {
        Stmt stmt;
        if (tokens.getType(0) == CONTINUETK) {
            stmt = new ContinueStmt();
        } else {
            stmt = new BreakStmt();
        }
        stmt.addChild(tokens.get());
        mustGetTerminal(stmt, SEMICN, "i");
        return stmt;
    }

    private Stmt lValStmt() {
        Stmt stmt;
        for (int i = 0; ; i++) {
            SyntaxType type = tokens.getType(i);
            if (type == ASSIGN) {
                if (tokens.getType(i + 1) == GETINTTK) {
                    stmt = new GetIntStmt();
                    stmt.addChild(lVal());
                    mustGetTerminal(stmt, ASSIGN, null);
                    mustGetTerminal(stmt, GETINTTK, null);
                    mustGetTerminal(stmt, LPARENT, null);
                    mustGetTerminal(stmt, RPARENT, "j");
                    break;
                } else {
                    stmt = new AssignStmt();
                    stmt.addChild(lVal());
                    mustGetTerminal(stmt, ASSIGN, null);
                    stmt.addChild(exp());
                    break;
                }
            } else if (type == SEMICN) {
                stmt = new ExpStmt();
                stmt.addChild(exp());
                break;
            } else if (type == EOF) {
                throw new RuntimeException("没有 }");
            }
        }
        mustGetTerminal(stmt, SEMICN, "i");
        return stmt;
    }

    private Stmt printfStmt() {
        /* 'printf' '(' FormatString {','Exp} ')' ';' */
        Stmt stmt = new PrintfStmt();
        mustGetTerminal(stmt, PRINTFTK, null);
        mustGetTerminal(stmt, LPARENT, null);
        mustGetTerminal(stmt, STRCON, null);
        while (tokens.getType(0) == COMMA) {
            stmt.addChild(getTerminal());
            stmt.addChild(exp());
        }
        mustGetTerminal(stmt, RPARENT, "j");
        mustGetTerminal(stmt, SEMICN, "i");
        return stmt;
    }

    private Stmt ifOrWhileStmt() {
        /* 'if' '(' Cond ')' Stmt [ 'else' Stmt ] */
        Stmt stmt;
        if (tokens.getType(0) == IFTK) {
            stmt = new IfStmt();
        } else {
            stmt = new WhileStmt();
        }
        tokens.get();
        mustGetTerminal(stmt, LPARENT, null);
        stmt.addChild(cond());
        mustGetTerminal(stmt, RPARENT, "j");
        stmt.addChild(stmt());
        if (tokens.getType(0) == ELSETK) {
            stmt.addChild(getTerminal());
            stmt.addChild(stmt());
        }
        return stmt;
    }

    private ReturnStmt returnStmt() {
        ReturnStmt stmt = new ReturnStmt();
        mustGetTerminal(stmt, RETURNTK, null);
        if (tokens.getType(0) != SEMICN) {
            stmt.addChild(exp());
        }
        mustGetTerminal(stmt, SEMICN, "i");
        return stmt;
    }

    private Exp exp() {
        /* Exp → AddExp */
        Exp exp = new Exp();
        exp.addChild(addExp());
        return exp;
    }

    private Cond cond() {
        /* Cond → LOrExp */
        Cond cond = new Cond();
        cond.addChild(lOrExp());
        return cond;
    }

    private LVal lVal() {
        /* LVal → Ident {'[' Exp ']'} */
        LVal lVal = new LVal();
        mustGetTerminal(lVal, IDENFR, null);
        while (tokens.getType(0) == LBRACK) {
            lVal.addChild(getTerminal());
            lVal.addChild(exp());
            mustGetTerminal(lVal, RBRACK, "k");
        }
        return lVal;
    }

    private  PrimaryExp primaryExp() {
        /* PrimaryExp → '(' Exp ')' | LVal | Number */
        PrimaryExp primaryExp = new PrimaryExp();
        SyntaxType type = tokens.getType(0);
        if (type == LPARENT) {
            primaryExp.addChild(getTerminal());
            primaryExp.addChild(exp());
            mustGetTerminal(primaryExp, RPARENT, "j");
        } else if (type == IDENFR) {
            primaryExp.addChild(lVal());
        } else {
            primaryExp.addChild(number());
        }
        return primaryExp;
    }

    private Number number() {
        /* Number → IntConst */
        Number number = new Number();
        mustGetTerminal(number, INTCON, null);
        return number;
    }

    private UnaryExp unaryExp() {
        /*  UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
         *      FIRST(PrimaryExp): {'(', Ident, IntCon }
         */
        UnaryExp unaryExp;
        SyntaxType type = tokens.getType(0);
        if (type == PLUS || type == MINU || type == NOT) {
            unaryExp = new UnaryExp();
            unaryExp.addChild(unaryOp());
            unaryExp.addChild(unaryExp());
        } else if (type == IDENFR && tokens.getType(1) == LPARENT) {
            unaryExp = new FuncCallUnaryExp();
            unaryExp.addChild(getTerminal());
            unaryExp.addChild(getTerminal());
            switch (tokens.getType(0)) {
                case INTCON:
                case LPARENT:
                case PLUS:
                case MINU:
                case NOT:
                case IDENFR:
                    unaryExp.addChild(funcRParams());
                    break;
                default:
                    break;
            }
            mustGetTerminal(unaryExp, RPARENT, "j");
        } else {
            unaryExp = new PrimaryUnaryExp();
            unaryExp.addChild(primaryExp());
        }
        return unaryExp;
    }

    private UnaryOp unaryOp() {
        /* UnaryOp → '+' | '−' | '!' */
        UnaryOp unaryOp = new UnaryOp();
        SyntaxType type = tokens.getType(0);
        assert type == PLUS || type == MINU || type == NOT;
        unaryOp.addChild(getTerminal());
        return unaryOp;
    }

    private FuncRParams funcRParams() {
        /* FuncRParams → Exp { ',' Exp } */
        FuncRParams funcRParams = new FuncRParams();
        funcRParams.addChild(exp());
        while(tokens.getType(0) == COMMA) {
            funcRParams.addChild(getTerminal());
            funcRParams.addChild(exp());
        }
        return funcRParams;
    }

    private MulExp mulExp() {
        /* MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp */
        MulExp mulExp = new MulExp();
        mulExp.addChild(unaryExp());
        while (true) {
            SyntaxType type = tokens.getType(0);
            if (type != MULT && type != DIV && type != MOD) {
                break;
            }
            MulExp tmp = new MulExp();
            tmp.addChild(mulExp);
            tmp.addChild(getTerminal());
            tmp.addChild(unaryExp());
            mulExp = tmp;
        }
        return mulExp;
    }

    private AddExp addExp() {
        /* AddExp → MulExp | AddExp ('+' | '−') MulExp */
        AddExp addExp = new AddExp();
        addExp.addChild(mulExp());
        while (true) {
            SyntaxType type = tokens.getType(0);
            if (type != PLUS && type != MINU) {
                break;
            }
            AddExp tmp = new AddExp();
            tmp.addChild(addExp);
            tmp.addChild(getTerminal());
            tmp.addChild(mulExp());
            addExp = tmp;
        }
        return addExp;
    }

    private compiler.syntax.cond.RelExp relExp() {
        /* RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp */
        RelExp relExp = new RelExp();
        relExp.addChild(addExp());
        while (true) {
            SyntaxType type = tokens.getType(0);
            if (type != LSS && type != GRE && type != LEQ && type != GEQ) {
                break;
            }
            RelExp tmp = new RelExp();
            tmp.addChild(relExp);
            tmp.addChild(getTerminal());
            tmp.addChild(addExp());
            relExp = tmp;
        }
        return relExp;
    }

    private compiler.syntax.cond.EqExp eqExp() {
        /* EqExp → RelExp | EqExp ('==' | '!=') RelExp */
        EqExp eqExp = new EqExp();
        eqExp.addChild(relExp());
        while (true) {
            SyntaxType type = tokens.getType(0);
            if (type != EQL && type != NEQ) {
                break;
            }
            EqExp tmp = new EqExp();
            tmp.addChild(eqExp);
            tmp.addChild(getTerminal());
            tmp.addChild(relExp());
            eqExp = tmp;
        }
        return eqExp;
    }

    private compiler.syntax.cond.LAndExp lAndExp() {
        /* LAndExp → EqExp | LAndExp '&&' EqExp */
        LAndExp lAndExp = new LAndExp();
        lAndExp.addChild(eqExp());
        while (tokens.getType(0) == AND) {
            LAndExp tmp = new LAndExp();
            tmp.addChild(lAndExp);
            tmp.addChild(getTerminal());
            tmp.addChild(eqExp());
            lAndExp = tmp;
        }
        return lAndExp;
    }

    private compiler.syntax.cond.LOrExp lOrExp() {
        /* LOrExp → LAndExp | LOrExp '||' LAndExp */
        LOrExp lOrExp = new LOrExp();
        lOrExp.addChild(lAndExp());
        while (tokens.getType(0) == OR) {
            LOrExp tmp = new LOrExp();
            tmp.addChild(lOrExp);
            tmp.addChild(getTerminal());
            tmp.addChild(lAndExp());
            lOrExp = tmp;
        }
        return lOrExp;
    }

    private ConstExp constExp() {
        /* ConstExp → AddExp */
        ConstExp constExp = new ConstExp();
        constExp.addChild(addExp());
        return constExp;
    }
}
