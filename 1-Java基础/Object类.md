# 概览

```java
public native int hashCode()
public boolean equals(Object obj)
protected native Object clone() throws CloneNotSupportedException
public String toString()
public final native Class<?> getClass()
protected void finalize() throws Throwable {}
public final native void notify()
public final native void notifyAll()
public final native void wait(long timeout) throws InterruptedException
public final void wait(long timeout, int nanos) throws InterruptedException
public final void wait() throws InterruptedException
```

# equals()

## equals 和 == 

- 对于基本类型，== 判断两个值是否相等，基本类型没有 equals() 方法。
- 对于引用类型，== 判断两个变量是否引用同一个对象，而 equals() 判断引用的对象是否等价。

## equals的工作原理

- 检查是否为同一个对象的引用，如果是直接返回 true；
- 检查是否是同一个类型，如果不是，直接返回 false；
- 将 Object 对象进行转型。
- 判断每个关键域是否相等。

# hashCode()

hashCode() 返回散列值，而 equals() 是用来判断两个对象是否等价

- equals的对象hashCode一定相同。
- hashCode相同的对象不一定equals。
- 为了保证两个散列值相同的对象一定equals，一般在重写equals方法的时候，需要重写hashCode方法。

在Object中hashCode的写法：

```java
	//我自己创建的 Base.Test 中:
		@Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

	//java.util.Objects;
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

	//java.util.Arrays;
    public static int hashCode(Object a[]) {
        if (a == null)
            return 0;

        int result = 1;

        for (Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }
```

将对象中的所有变量打包一起使用Arrays.hashCode(values)方法。在这个方法里面，传入基本数据类型会自动装箱。和直接使用数字计算出来的结果不相同。

在自己定义的类中可以自己定义hashCode的实现方式。但是需要将不相等的对象要尽量分布在不相等的数值上，就和通信原理中的“频分复用‘’一样。



# toString()

在没有重写toString方法的时候，对象的toString的返回值是：

```java
11661Base.TestClass@2d8d
```

通常，对toString进行重写：

```java
//自行重写的toString方法:
@Override
public String toString() {
	return "TestClass:{" +
		"x=" + x +
		", y=" + y +
		", z=" + z +
		'}';
}
```

这样的toString的返回值是：

```java
TestClass:{x=11, y=12, z=13}
```



# clone()

## cloneable

- clone() 是 Object 的 protected 方法，它不是 public，一个类不显式去重写 clone()，其它类就不能直接去调用该类实例的 clone() 方法。
- 注意，clone() 方法并不是 Cloneable 接口的方法，而是 Object 的一个 protected 方法，而Cloneable是一个空接口。
- Cloneable 接口只是规定，如果一个类没有实现 Cloneable 接口又调用了 clone() 方法，就会抛出 CloneNotSupportedException。

Object的clone方法来使用的是受保护的native方法：

```java
protected native Object clone() throws CloneNotSupportedException;
```

使用方式如下：

```java
TestClass test = new TestClass(11, 12, 13);
try {
	TestClass testClone = (TestClass) test.clone();
} catch (CloneNotSupportedException e) {
	e.printStackTrace();
}
```

重写 clone() 得到以下实现：

```java
//对clone进行重写:
@Override
protected TestClass clone() throws CloneNotSupportedException {
	return new TestClass(this.x, this.y, this.z);
}
```

```java
TestClass testClone = null;
try {
	testClone = test.clone();
} catch (CloneNotSupportedException e) {
	e.printStackTrace();
}
```

## 浅克隆

拷贝对象和原始对象的引用类型引用同一个对象。

浅克隆的重写方式：

```java
//对clone进行重写:
@Override
protected TestClass clone() throws CloneNotSupportedException {
	return this;
}
```

主函数测试程序：

```java
TestClass test = new TestClass(11, 12, 13);
TestClass testClone = test.clone();

test.setX(10);
System.out.println(test.getX() + " " + testClone.getX());
System.out.println(test.equals(testClone));
```

运行结果：

```java
10 10
true
```

这个结果表明，在克隆的时候，只是将克隆体的地址指向了原本的对象的地址。这样操作的好处在于方便快捷。坏处在于一旦原本的对象的信息改变了，那么这个副本的对象的信息也将改变。

## 深克隆  

拷贝对象和原始对象的引用类型引用不同对象。

```java
    //对clone进行重写:
    @Override
    protected TestClass clone() throws CloneNotSupportedException {
        return new TestClass(this.x, this.y, this.z);
    }
```

对这个方式进行测试的程序和前面的相同，但是运行的结果如下：

```java
10 11
false
```

这就是深度克隆，将原本的对象信息原封不动的复制一份，存储在一个新的地址。这个结果不会随原本的结果变化而变化。

## clone() 的替代方案 

使用 clone() 方法来拷贝一个对象即复杂又有风险，它会抛出异常，并且还需要类型转换。Effective Java 书上讲到，最好不要去使用 clone()，可以使用拷贝构造方法或者拷贝工厂来拷贝一个对象。

克隆构造方法：

```java
//克隆构造方法
public TestClass(TestClass origin) {
    this(origin.x, origin.y, origin.z);
}
```

```java
TestClass testClone = new TestClass(test);
System.out.println(test.equals(testClone));
```

测试结果：

```java
true
```

# getClass()

getClass()方法是一个获取当前的对象的类型的类的对象。

```java
TestClass test = new TestClass(11, 12, 13);

System.out.println(test.getClass().toString());
System.out.println(test.getClass().getDeclaredFields()[0].toString());
```

运行结果：

```
class Base.TestClass
private int Base.TestClass.x
```

每一个Class类型的的对象中都保留着一个类的的完整的信息。其中包括域、方法等信息。

例如上面的第二个`println`信息。

# finalize()

finalize()是Object类中的方法。当垃圾回收器将要回收对象所占内存之前被调用，即当一个对象被虚拟机宣告死亡时会先调用它finalize()方法，让此对象处理在它被回收之前的最后工作。

当以恶搞对象在被判定应当被回收的时候，仍然有机会不被回收。但是又下面的两个条，对象覆写了finalize()方法：在finalize()方法中重新引用到"GC  Roots”链上。
finalize()只会在对象内存回收前被调用一次。

finalize()的调用具有不确定行，只保证方法会调用，但不保证方法里的语句任务会被执行完。

# wait(),notify()

wait和notify它们是Object的方法。

这一组信号一般和synchronized关键字组合使用。前面在synchronized部分有过详细的举例。

调用wait方法时，线程进入等待状态，并且会释放synchronized的锁。

一个线程使用了wait的方法之后，重新苏醒条件是：其他的线程对这个锁使用了notify方法，并且这个锁没有被占用。

得到了notify信号之后，线程从执行wait的地方恢复执行。

添加参数的wait和notify就是表示有时间的延迟的wait和notify。

**wait() 和 sleep() 的区别** 

wait() 是 Object 的方法，而 sleep() 是 Thread 的静态方法。

wait() 会释放锁，sleep() 不会。

# 程序清单

```java
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
```

