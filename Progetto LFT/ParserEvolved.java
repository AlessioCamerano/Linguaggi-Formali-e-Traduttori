import java.io.*;

public class ParserEvolved {
    protected Lexer lex;
    protected BufferedReader pbr;
    protected Token look;

    public ParserEvolved(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    public void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    public void error(String s) {
        throw new Error("near line " + lex.line + ": " + s);
    }

    public void match(int t) {
        if (look.tag == t) {
           if (look.tag != Tag.EOF) move();
        } else {
            error("syntax error");
        }
    }

    public void prog(){
        switch (look.tag){
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{':
                statlist();
                match(Tag.EOF);
                break;
            default:
               error("prog()");
        }
    }

    private void statlist(){
        switch (look.tag){
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{':
                stat();
                statlistp();
                break;
            default:
                error("statlist()");
        }
    }

    private void statlistp(){
        switch (look.tag){
            case ';':
                move();
                stat();
                statlistp();
                break;
            case Tag.EOF:
            case '}':
            case Tag.ELSE:
                break;
            default: 
                error("statlistp()");
        }
    }

    private void stat(){
        switch (look.tag){
            case Tag.ASSIGN:
                move();
                assignlist();
                break;
            case Tag.PRINT:
                move();
                match('(');
                exprlist();
                match(')');
                break;
            case Tag.READ:
                move();
                match('(');
                idlist();
                match(')');
                break;
            case Tag.FOR:
                move();
                match('(');
                stat1();
                break;
            case Tag.IF:
                move();
                match('(');
                bexpr();
                match(')');
                stat();
                stat2();
                break;
            case '{':
                move();
                statlist();
                match('}');
                break;
            default:
                error("stat()");
        }
    }

    private void assignlist(){
         switch(look.tag){
            case '[':
                move();
                expr();
                match(Tag.TO);
                idlist();
                match(']');
                assignlistp(); 
                break;
            default:
                error("assignlist()");
        } 
    }

    private void assignlistp(){
        switch (look.tag){
            case '[':
                move();
                expr();
                match(Tag.TO);
                idlist();
                match(']');
                assignlistp(); 
                break;
            case ';':
            case Tag.END:
            case Tag.EOF:
            case '}':
            case Tag.ELSE:
                break;
            default:
                error("assignlist()");
        }
    }

    private void idlist(){
        switch (look.tag){
            case Tag.ID:
                move();
                idlistp();
                break;
            default:
                error("idlist()");
        }
    }

    private void idlistp(){
        switch (look.tag){
            case ',':
                move();
                match(Tag.ID);
                idlistp();
                break;
            case ']':
            case ')':
                break;
            default:
                error("idlistp()");
        }
    }

    private void bexpr(){
        switch(look.tag){
            case Tag.RELOP:
                move();
                expr();
                expr();
                break;
            default:
                error("bexpr()");
        }
    }

    private void expr(){
        switch(look.tag){
            case '+':
            case '*':
                move();
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
            case '/':
                move();
                expr();
                expr();
                break;
            case Tag.NUM:
            case Tag.ID:
                move();
                break;
            default:
                error("expr()");
        }
    }

    private void exprlist(){
        switch (look.tag){
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            default:
                error("exprlist()");
        }
    }

    private void exprlistp(){
        switch(look.tag){
            case ',':
                move();
                expr();
                exprlistp();
                break;
            case ')':
                break;
            default:
                error("exprlistp()");
        }
    }

    private void stat1(){
        switch(look.tag){
            case Tag.ID:
                move();
                match(Tag.INIT);
                expr();
                match(';');
                bexpr();
                match(')');
                match(Tag.DO);
                stat();
                break;
            case Tag.RELOP:
                bexpr();
                match(')');
                match(Tag.DO);
                stat();
                break; 
            default:
                error("stat1()");
        }
    }

    private void stat2(){
        switch(look.tag){
            case Tag.ELSE:
                move();
                stat();
                match(Tag.END);
                break;
            case Tag.END:
                move();
                break;
            default:
                error("stat2()");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\aless\\Desktop\\Progetto LFT\\test.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserEvolved parser = new ParserEvolved(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
