import java.lang.Character;
public class C1E3 {
    public static boolean isPari(char ch){
        if (ch == '0' || ch== '2' || ch == '4' || ch== '6' || ch== '8'){
            return true;
        }
        return false;
    }

    public static boolean isDispari(char ch){
        if (ch == '1' || ch== '3' || ch == '5' || ch== '7' || ch== '9'){
            return true;
        }
        return false;
    }

    public static boolean scan(String s){
        int state = 0;
        int i = 0;
        while(state >= 0 && i < s.length()){
            final char ch= s.charAt(i++);
            switch(state){
                case 0:
                    if (isDispari(ch)){
                        state= 1;
                    } else if (isPari(ch)){
                        state= 2;
                    } else if(Character.isLetter(ch)){
                        state= -1;
                    }
                    break; 
                case 1: 
                    if (isDispari(ch)){
                        state= 1;
                    } else if (isPari(ch)){
                        state= 2;
                    } else if (ch >= 'L' && ch <= 'Z' || ch >='l' && ch <='z'){
                        state =3;
                    } else if(ch >= 'A' && ch <= 'K' || ch >= 'a' && ch <='k'){
                        state= -1;
                    }
                    break;
                case 2:
                    if (isDispari(ch)){
                        state= 1;
                    } else if (isPari(ch)){
                        state= 2;
                    } else if (ch >= 'L' && ch <= 'Z' || ch >='l' && ch <='z'){
                        state= -1;
                    } else if(ch >= 'A' && ch <= 'K' || ch >= 'a' && ch <='k'){
                        state= 4;
                    }
                        break;
                case 3:
                    if (ch >= '0' && ch <= '9'){
                        state= -1;
                    } else if(Character.isLetter(ch)){
                        state= 3;
                    }
                    break;
                case 4:
                    if (ch >= '0' && ch <= '9'){
                        state= -1;
                    } else if(Character.isLetter(ch)){
                        state= 4;
                    }
                    break; 
            }
        }
        System.out.println(state);
        return state == 3 || state == 4;
    }

    public static void main(String[] args){
        System.out.println(scan("123456Bianchi") ? "ok" : "nope");
    }
}
