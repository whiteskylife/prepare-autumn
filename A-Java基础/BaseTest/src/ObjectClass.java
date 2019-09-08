/**
 * 中建
 *
 * @author thisxzj
 * @date 2019-08-06
 * @time 23:08
 */


public class ObjectClass {
    public static void main(String[] args) {
        TestClass test = new TestClass(11, 12, 13);

        System.out.println(test.getClass().toString());
        System.out.println(test.getClass().getDeclaredFields()[0].toString());

    }
}

class TestClass implements Cloneable {
    private int x;
    private int y;
    private int z;

    TestClass(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    /**
     * 重写hashCode 方法
     *
     * @return hashCode
     */
    @Override
    public int hashCode() {
        int ans = 0;
        int[] arr = {x, y, z};
        for (int i = 0; i < arr.length; i++) {
            ans += (31 * ans + arr[i]);
        }
        return ans;
    }

    /**
     * 重写的toString方法
     *
     * @return string
     */
    @Override
    public String toString() {
        return "TestClass:{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }


    /**
     * 对clone进行重写:
     *
     * @return 克隆出来de 对象
     * @throws CloneNotSupportedException 不支持克隆异常
     */
    @Override
    protected TestClass clone() throws CloneNotSupportedException {
        //return this;
        return new TestClass(this.x, this.y, this.z);
    }
}