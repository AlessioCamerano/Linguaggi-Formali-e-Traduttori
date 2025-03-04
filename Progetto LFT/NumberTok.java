public class NumberTok extends Token {
	public int lexeme;
	public NumberTok(int tag, int n) {super(tag); lexeme=n;
	}
	public int getvalue(){
		return lexeme;
	}

	public String toString() { return "<" + tag + ", " + lexeme + ">"; }
}
