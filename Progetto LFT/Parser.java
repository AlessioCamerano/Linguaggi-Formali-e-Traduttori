import java.io.*;

public class Parser {
    protected Lexer lex;
    protected BufferedReader pbr;
    protected Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    public void move() { //passa all'elemento successivo
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    public void error(String s) { //ERROR
        throw new Error("near line " + lex.line + ": " + s);
    }

    public void match(int t) { //controlla e poi move()
        if (look.tag == t) {
           if (look.tag != Tag.EOF) move();
        } else {
            error("syntax error");
        }
    }

    public void start() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            expr();
            match(Tag.EOF);
        } else {
            error("start()");
        }
    }

    private void expr() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            term();
            exprp();
        } else {
            error("expr()");
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
            case '-':
                move();
                term();
                exprp();
                break;
            case Tag.EOF:
            case ')':
                break;
            default:
                error("exprp()");
        }
    }

    private void term() {
        if (look.tag == '(' || look.tag == Tag.NUM) {
            fact();
            termp();
        } else {
            error("term()");
        }
    }

    private void termp() {
        switch (look.tag) {
            case '*':
            case '/':
                move();
                fact();
                termp();
                break;
            case '+':
            case '-':
            case Tag.EOF:
            case ')':
            break;
            default: 
                error("termp()");
        }
    }

    private void fact() {
        if (look.tag == '(') {
            move();
            expr();
            match(')');
        } else if (look.tag == Tag.NUM) {
            move();
        } else {
            error("fact()");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\aless\\Desktop\\Progetto LFT\\test.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
