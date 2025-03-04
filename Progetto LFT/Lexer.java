import java.io.*; 
//import java.util.*;

public class Lexer {

    public int line = 1;
    private char peek = ' ';
    private boolean flag = false;

    private Token loop(BufferedReader br){
        while (true) {
            if(peek != '*'){
            readch(br);
            }
            if (peek == (char)-1) { //ERROR
                System.err.println("Comment not terminated");
                return null;
            }
            if (peek == '*') { //controllo sul commento 
                readch(br);
                if (peek == '/') {
                    return lexical_scan(br);
                }
            }
        }
    } 
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read(); //leggi il valore e lo salvi come char
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }


    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++; //vado a capo 
            readch(br);
        }
        

        switch (peek) { //scorro i casi possibili
            case '!':
                peek = ' ';
                return Token.not;
			case '(':
				peek = ' ';
                return Token.lpt;
			case ')':
                peek = ' ';
                return Token.rpt;
			case '[':
                peek = ' ';
                return Token.lpq;
			case ']':
                peek = ' ';
                return Token.rpq;
			case '{':
                peek = ' ';
                return Token.lpg;
			case '}':
                peek = ' ';
                return Token.rpg;
			case '+':
                peek = ' ';
                return Token.plus;
			case '-':
                peek = ' ';
                return Token.minus;
            case '_': //per gli id
                String f="";
                while (Character.isLetterOrDigit(peek) || peek=='_'){
                    if (Character.isLetterOrDigit(peek)){
                        flag = true;
                    }
                    f+= peek;
                    readch(br);
                }
                if (!flag){ //ERROR
                    System.err.println("Wrong identifier");
                    return null;
                }
                    return new Word(Tag.ID, f);
			case '*':
                peek = ' ';
                return Token.mult;
			case '/': //commenti 
                readch(br);
                if (peek=='/'){
                    while (peek != '\n' && peek != (char)-1){
                        readch(br);
                    }
                    return lexical_scan(br);
                } else if (peek == '*'){
                    loop(br);
                    return lexical_scan(br);
                } else {
                return Token.div;
                }
			case ';':
                peek = ' ';
                return Token.semicolon;
			case ',':
                peek = ' ';
                return Token.comma;
            case '&': //relazioni
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }
            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after | : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else {
                    if(peek=='>'){
                        peek=' ';
                        return Word.ne;
                    }
                return Word.lt;
                }
            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                }
                return Word.gt;
	
            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    System.err.println("Erroneous character"
                            + " after = : "  + peek );
                    return null;
                }
            case ':':
                readch(br);
                if(peek == '='){
                    peek = ' ';
                    return Word.init; 
                }else{
                    System.out.println("Erroneous character"
                            + "after : " + peek);
                            return null;
                }
            case (char)-1:
                return new Token(Tag.EOF); //finita la riga

            default: 
                if (Character.isLetter(peek)) { //caso lettere
                    String s = ""; 
                    while(Character.isLetter(peek)){
                         s = s + peek; 
                        readch(br);
                    }
                    switch (s) {
                    case "assign":
                        return Word.assign;
                    case "to":
                        return Word.to;
                    case "if":
                        return Word.iftok;
                    case "else":
                        return Word.elsetok;
                    case "do":
                        return Word.dotok;
                    case "for":
                        return Word.fortok;
                    case "begin":
                        return Word.begin; 
                    case "end":
                        return Word.end;
                    case "print":
                        return Word.print; 
                    case "read":
                        return Word.read;
                    default: 
                        while (Character.isLetterOrDigit(peek) || peek=='_'){ //seconda parte degli id
                            s+= peek;
                            readch(br);
                        }
                        return new Word(Tag.ID, s);
                    }


                } else if (Character.isDigit(peek)) { //caso numeri
                    int n = 0;
                    if (Character.getNumericValue(peek) == 0){
                        readch(br);
                        if (Character.isDigit(peek)){
                            System.err.println("Errore: un numero inizia con 0 e continua con altre cifre");
                            return null;
                        } else {
                            return new NumberTok(Tag.NUM, n);
                        }
                    }
                    while(Character.isDigit(peek)){
                        n = n*10+Character.getNumericValue(peek); 
                        readch(br);
                    }
                    return new NumberTok(Tag.NUM, n);
                } else {
                        System.err.println("Erroneous character: " 
                                + peek);
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\aless\\Desktop\\Progetto LFT\\test.txt"; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok); //stampa ogni token
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();} 
    }

}