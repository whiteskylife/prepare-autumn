/**
 * @author 袋鼠云 - 中建
 */


public class ThisSuperTest extends AbstractClass {
    private int k = 0;

    public ThisSuperTest(int i, int j, int k) {
        super(i, j);
        this.k = k;
    }

    public ThisSuperTest() {
        this(0, 0, 0);
    }

    @Override
    public int abstractFunc() {
        super.j = 10;
        return 10;
    }

    public void printAnsOfAbstractFunc() {
        int i = this.abstractFunc();
        System.out.println(i);
    }

    public void printAnsOfFunc(int i, int j) {
        int ans = super.func(i, j);
        System.out.println(ans);
    }
}

class Main {
    public static void main(String[] args) {
        ThisSuperTest test = new ThisSuperTest();
        test.printAnsOfAbstractFunc();
        test.printAnsOfFunc(10, 5);
    }
}