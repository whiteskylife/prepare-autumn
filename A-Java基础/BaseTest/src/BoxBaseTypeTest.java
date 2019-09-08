/**
 * Author thisxzj
 * SLOGAN:让未来变成现在
 * Date & Time:     2019-08-05 23:51
 *
 * @author 袋鼠云 - 中建
 */


public class BoxBaseTypeTest {
    public static void main(String[] args) {
        final int NUM = 129;
        Integer i1 = NUM;
        Integer i2 = Integer.valueOf(NUM);
        Integer i3 = new Integer(NUM);

        System.out.println("i1 ==? i2 " + (i1 == i2));
        System.out.println("i1 ==? i3 " + (i1 == i3));
        System.out.println("i2 ==? i3 " + (i2 == i3));

        System.out.println("i1 equals ? i2 " + (i1.equals(i2)));
        System.out.println("i1 equals ? i3 " + (i1.equals(i3)));
        System.out.println("i2 equals ? i3 " + (i2.equals(i3)));
    }
}
