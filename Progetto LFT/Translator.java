 import java.io.*;
 
 public class Translator{
    private Lexer lex; 
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count_idlistp = 0;

    public Translator(Lexer l, BufferedReader br){
        lex = l;
        pbr = br;
        move();
    }

    void move(){ 
        look = lex.lexical_scan(pbr); 
        System.out.println("token = " + look);
    }

    void error(String s){ 
	    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t){ 
        if (look.tag == t) { 
            if (look.tag != Tag.EOF) move(); 
        } else {
            error("syntax error");
        }
    }

    public void prog(){ 
        switch(look.tag){
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case '{':
            case Tag.IF:
                int lnext_prog =code.newLabel();
                statlist(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try{
                    code.toJasmin();
                }
                catch(java.io.IOException e){
                    System.out.println("IO error\n");
                }
                break;
            default: 
                error("prog()");
        }
    }

    public void statlist(int lnext){ 
        switch(look.tag){
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case '{':
            case Tag.IF: 
                int lnext_stat= code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp(lnext);
                break;
            default:
                error("statlist()");
        }
    }

    public void statlistp(int lnext){
        switch (look.tag) {
            case ';':
                move();
                int lnext_stat= code.newLabel();
                stat(lnext_stat);
                code.emitLabel(lnext_stat);
                statlistp(lnext);
                break;
            case '}':
            case Tag.EOF:
                code.emit(OpCode.GOto, lnext);
                break;
            default: 
                error("statlistp()");
        }
    }

    public void stat(int lnext){
        int label_true, label_false;
        switch(look.tag){
            case Tag.ASSIGN:
                move();
                assignlist();
                code.emit(OpCode.GOto, lnext);
                break;
            case Tag.PRINT:
                move();
                match('(');
                printcase();
                match(')');
                code.emit(OpCode.GOto, lnext);
                break;
            case Tag.READ:
                move();
                match('(');
                idlist(OpCode.invokestatic, 0);
                match(')');
                code.emit(OpCode.GOto, lnext);
                break;
            case Tag.FOR:  
                int label_loop= code.newLabel();
                label_true= code.newLabel();
                move();
                match('(');
                stat1();
                code.emitLabel(label_loop);
                bexpr(label_true, lnext);
                match(')');
                match(Tag.DO);
                code.emitLabel(label_true);
                stat(label_loop);
                break; 
            case Tag.IF:
                label_true= code.newLabel();
                label_false= code.newLabel();
                move();
                match('(');
                bexpr(label_true, label_false);
                match(')');
                code.emitLabel(label_true);
                stat(lnext);
                code.emitLabel(label_false);
                stat2(lnext);
                match(Tag.END);
                break;
            case '{':
                move();
                statlist(lnext);
                match('}');
                break;
            default:
                error("stat()");
        }
    }

    public void printcase(){
        switch(look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                code.emit(OpCode.invokestatic, 1);
                exprlistp(OpCode.invokestatic, 1);
                break;
            default:
                error("printcase()");
        }
    }

    public void stat1(){ 
        switch(look.tag){
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme); 
                if (id_addr==-1){
                    id_addr = count_idlistp;
                    st.insert(((Word)look).lexeme,count_idlistp++);
                } 
                move();
                match(Word.init.tag);
                expr();
                code.emit(OpCode.istore, id_addr);
                match(';');
                break;
            case Tag.RELOP:
                break;
            default:
                error("stat1()");
        }
    }

    public void stat2(int lnext){
        switch (look.tag) {
            case Tag.ELSE:
                move();
                stat(lnext);
                break;
            case Tag.END:
                break;
            default:
                error("stat2()");
        }
    }

    public void assignlist(){
        match('[');
        expr();
        match(Tag.TO);
        idlist(OpCode.dup, 0);
        code.emit(OpCode.pop);
        match(']');
        assignlistp();
    }

    public void assignlistp(){
        switch(look.tag){
            case '[':
                move();
                expr();
                match(Tag.TO);
                idlist(OpCode.dup, 0);
                code.emit(OpCode.pop);
                match(']');
                assignlistp();
                break;
            case Tag.END:
            case '}':
            case Tag.ELSE:
            case ';':
                break;
            default:
                error("assignlistp()");
        }
    }

    public void idlist(OpCode Op, int x){
        switch(look.tag) {
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);//lookupadress controlla se è già stata dichiarata o meno la variabile
                if (id_addr==-1){
                    id_addr = count_idlistp;
                    st.insert(((Word)look).lexeme,count_idlistp++);
                }
                code.emit(Op, x);
                code.emit(OpCode.istore, id_addr);
                match(Tag.ID);
                idlistp(Op, x);
                break;
            default: 
                error("idlist()");
            }
    }

    public void idlistp(OpCode Op, int x){
        switch(look.tag){
            case ',':
                match(',');
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                if (id_addr==-1){
                    id_addr = count_idlistp;
                    st.insert(((Word)look).lexeme,count_idlistp++);
                }
                code.emit(Op, x);
                code.emit(OpCode.istore, id_addr); 
                match(Tag.ID);
                idlistp(Op, x);
                break;
            case ')':
            case ']':
                break;
            default:
                error("idlistp()");
        }
    }
    
    public void bexpr(int label_true, int label_false){
        switch(look.tag){
            case Tag.RELOP:
                String relop = ((Word)look).lexeme;
                match(Tag.RELOP);
                expr();
                expr();
                switch (relop){
                    case "<=":
                        code.emit(OpCode.if_icmple, label_true);              
                        break;
                    case ">=":
                        code.emit(OpCode.if_icmpge, label_true);
                        break;
                    case ">":
                        code.emit(OpCode.if_icmpgt, label_true);
                        break;
                    case "<":
                        code.emit(OpCode.if_icmplt, label_true);
                        break;
                    case "<>":
                        code.emit(OpCode.if_icmpne, label_true);
                        break;
                    case "==":
                        code.emit(OpCode.if_icmpeq, label_true);
                        break; 
                }
                code.emit(OpCode.GOto, label_false);
                break;
            default:
                error("bexpr()");
        }
    }

    public void expr(){
        switch(look.tag){
            case '+':
                move();
                match('(');
                exprlist(OpCode.iadd);
                match(')');
                break;
            case '-':
                move();
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                move();
                match('(');
                exprlist(OpCode.imul); 
                match(')');
                break;
            case '/':
                move();
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok)look).lexeme);
                move();
                break;
            case Tag.ID:
                int id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (id_addr==-1){
                       error("variabile non dichiarata");
                    }
                    code.emit(OpCode.iload, id_addr);
                    move();
                    break;
            default:
                error("expr()");
        }
    }

    public void exprlist(OpCode Op){
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.ID:
            case Tag.NUM:
                expr();
                exprlistp(Op, 0);
                break;
            default:
                error("exprlist()");
        }
    }

    public void exprlistp(OpCode Op, int x){
        switch (look.tag){
        case ',':
            move();
            expr();
            code.emit(Op, x);
            exprlistp(Op, x);
        case ')':
            break;
        default: 
            error("exprlistp()");
        }
    }

    public static void main(String[] args){
        Lexer lex = new Lexer(); 
        String path = "C:\\Users\\aless\\Desktop\\Progetto LFT\\test.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator parser = new Translator(lex, br);
            parser.prog(); 
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}