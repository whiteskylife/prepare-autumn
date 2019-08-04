/**
 * @author thisxzj
 */
public class Main {


    public int func(long a, int b) {
        return 0;
    }

    int func(int a, long b) {
        return 0;
    }


    public static void main(String[] args) {
        Main main = new Main();
        System.out.println("Hello World!" + " " + main.func(2, 3L));
    }
}