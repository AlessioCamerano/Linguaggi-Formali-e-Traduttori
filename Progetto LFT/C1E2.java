public class C1E2 {

    public static boolean scan( String s){
    int state= 0;
    int i = 0;
    while(state >= 0 && i < s.length()){
        final char ch= s.charAt(i++);
        switch(state) {
            case 0:
               if (ch >= '0' && ch <= '9'){
                state = 2;
               } else if (ch >= 'a' && ch <= 'z'){
                state = 1;
               } else if (ch == '-'){
                state = 3;
               } else {
                state = -1;
               }
               break;

            case 1:
               if ((ch =='-') || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')){
                state = 1;
               } else {
                state= -1;
               }
               break;
            case 2:
                if ((ch =='-') || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')){
                state = 2;
               } else {
                state= -1;
               }
               break;
            case 3:
               if (ch == '-'){
                state= 3;
               } else if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z')){
                state= 1;
               }
               break;
        }
    }
    return state == 1;
    }

    public static void main(String[] args){
        System.out.println(scan("flag1") ? "ok" : "nope");
    }
}