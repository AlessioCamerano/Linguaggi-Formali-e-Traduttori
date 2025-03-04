import java.io.*;

public class C4E1 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public C4E1(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }
    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }
    
     void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
         } else {
             error("syntax error");
         }
    }

    public void start() {
        int expr_val;
        switch (look.tag){
            case '(', Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                System.out.println(expr_val);
                break;
            default:
                error("start()");
        }
    }   
    
    private int expr() {
        int term_val, exprp_val;
        switch (look.tag){
            case '(', Tag.NUM:
                term_val = term();
                exprp_val = exprp(term_val);
                return exprp_val;
            default:
                error("expr()");
                return -1;
        }
    }

    private int exprp(int exprp_i) {
        int term_val, exprp_val;
        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                return exprp_val;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                return exprp_val;
            case Tag.EOF, ')':
                return look.tag;
            default:
                error("exprp()");
                return -1;
        }
    }
    
    private int term() {
        int fact_val, termp_val;
        switch (look.tag){
            case '(', Tag.NUM:
                fact_val= fact();
                termp_val= termp(fact_val);
                return termp_val;
            default:
                error("term()");
                return -1;
        }  
    }

    private int termp(int termp_i) {
        int fact_val, termp_val;
        switch(look.tag){
            case '*':
                match('*');
                fact_val= fact();
                termp_val= termp(termp_i*fact_val);
                return termp_val;
            case '/':
                match('/');
                fact_val= fact();
                termp_val= termp(termp_i/fact_val);
                return termp_val;
            case '+', '-', Tag.EOF, ')':
                return termp_i;
            default:
                error("termp()");
                return -1;
        }
    }
    
    private int fact() {
        int expr_val;
        switch(look.tag){
            case '(':
                match('(');
                expr_val= expr();
                match(')');
                return expr_val;
            case Tag.NUM:
                int c= look.getvalue();
                match(Tag.NUM);
                return c;         
            default:
                error("fact()");
                return -1;
        }
    }


public static void main(String[] args) {
    Lexer lex = new Lexer();
    String path = "C:\\Users\\aless\\Desktop\\Progetto LFT\\test.txt"; // il percorso del file da leggere
try {
    BufferedReader br = new BufferedReader(new FileReader(path));
    C4E1 valutatore = new C4E1(lex, br);
    valutatore.start();
    System.out.println("Input OK");
    br.close();
} catch (IOException e) {e.printStackTrace();}
}
}
