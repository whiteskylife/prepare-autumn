# 反射

在Java 运行时可以访问、检测和修改它本身状态或行为的一种能力，它允许运行中的Java程序获取自身的信息，并且可以操作类或者对象的内部的属性。

反射的重点在于`runtime`阶段的获取类信息和调用类方法，那么当你的编码过程中中有“部分信息是source阶段不清晰，需要在runtime阶段动态临时加载”这种场景，反射就可以派上用场了。

## 测试使用的类

```java
package com.thisxzj.reflect;

/**
 * date    2019-09-26
 * time    23:12
 *
 * @author thisxzj - 中建
 */


public class User {
    private String name;
    private int id;
    private String location;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User(String name, int id, String location) {
        this.name = name;
        this.id = id;
        this.location = location;
    }

    public User() {
        this("null", 0, "null");
    }

    private String sayHello() {
        String ans = "my name is " + name + ", I am from " + location;
        System.out.print(ans);
        return ans;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", location='" + location + '\'' +
                '}';
    }
}
```

## 反射过程

```java
package com.thisxzj.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * date    2019-09-26
 * time    23:16
 *
 * @author thisxzj - 中建
 */


public class MainReflect {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        /*
         * ⼀一:获取 Class 对象的三种⽅方式
         */
        Class c1 = Class.forName("com.thisxzj.reflect.User");
        Class c2 = User.class;
        User userClass = new User("", 0, "");
        Class c3 = userClass.getClass();
        System.out.println(c1 + "\n" + c2 + "\n" + c3);


        /*
         * ⼆:通过反射来创建类的实例例
         */
        User user1 = (User) c1.newInstance();
        Constructor constructor = c1.getConstructor();
        User user2 = (User) constructor.newInstance();

        System.out.println(user1.toString() + "\n" + user2.toString());

        /*
         * 三:通过反射获取类的信息
         */
        System.out.println("父类:\n" + c1.getSuperclass().getName());
        System.out.println("\n构造方法:\n" + c1.getConstructor());
        System.out.println("\n字段:");
        Field[] fields = c1.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getType() + " " + field.getName());
        }

        System.out.println("\n方法:");
        Method[] methods = c1.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }
}
```

## 结果

```java
class com.thisxzj.reflect.User
class com.thisxzj.reflect.User
class com.thisxzj.reflect.User
User{name='null', id=0, location='null'}
User{name='null', id=0, location='null'}
父类:
java.lang.Object

构造方法:
public com.thisxzj.reflect.User()

字段:
class java.lang.String name
int id
class java.lang.String location

方法:
toString
getName
getLocation
setName
getId
setLocation
setId
sayHello
```

当然，其中使用不同的获取类的信息的方法，可以获得不同的权限的方法、字段，具体不表。

## 使用

很多，也许是多数 Java 框架都会适度使用反射。如果编写的架构足够灵活，在运行时之前都不知道要处理什么代码，那么通常都需要使用反射。插入式架构、调试器、代码 浏览器和 REPL 类环境往往都会在反射的基础上实现。

反射在测试中也有广泛应用，例如，JUnit和TestNG库都用到了反射，而且创建模拟对象也要使用反射。如果你用过任何一个Java框架，即便没有意识到，也几乎可以确定，你使用的是具有反射功能的代码。

在自己的代码中使用反射API时一定要知道，获取到的对象几乎所有信息都未知，因此处理起来可能很麻烦。

只要知道动态加载的类的一些静态信息(例如，加载的类实现一个已知的接口)，与这个类交互的过程就能大大简化，减轻反射操作的负担。

使用反射时有个常见的误区:试图创建能适用于所有场合的反射框架。正确的做法是，只 处理当前领域立即就能解决的问题。

# 代理

## 静态代理

首先,定义接口和接口的实现类,然后定义接口的代理对象，将接口的实例注入到代理对象中，然后通过代理对象去调用真正的实现类，实现过程非常简单也比较容易理解，静态代理的代理关系在编译期间就已经确定了的。它适合于代理类较少且确定的情况。缺点是只适用委托方法少的情况下。

### 接口

```java
package com.thisxzj.proxy.staticproxy;

/**
 * date    2019-09-26
 * time    23:46
 *
 * @author thisxzj - 中建
 */


public interface IHelloService {
    String sayHello(String name);
}

```

### 被代理方法

```java
package com.thisxzj.proxy.staticproxy;

/**
 * date    2019-09-26
 * time    23:47
 *
 * @author thisxzj - 中建
 */


public class HelloService implements IHelloService {

    @Override
    public String sayHello(String name) {
        return "hello " + name + "!";
    }
}
```

### 实现代理

```java
package com.thisxzj.proxy.staticproxy;

/**
 * date    2019-09-26
 * time    23:49
 *
 * @author thisxzj - 中建
 */


public class StaticProxyHello implements IHelloService {
    private IHelloService helloService = new HelloService();

    @Override
    public String sayHello(String userName) {
        String s = helloService.sayHello(userName);
        System.out.println(s);
        return s;
    }
}
```

### 测试

```java
package com.thisxzj.proxy.staticproxy;

/**
 * date    2019-09-26
 * time    23:52
 *
 * @author thisxzj - 中建
 */


public class Main {
    public static void main(String[] args) {
        StaticProxyHello staticProxyHello = new StaticProxyHello();
        staticProxyHello.sayHello("xzj");
    }
}
```

## 动态代理

在 Java 的动态代理理中，主要涉及`java.lang.reflect` 中的 `Proxy`和 `InvocationHandler`类。

我们需要⼀个实现 InvocationHandler 接⼝的中间类。这个接口只有一个⽅法，invoke ⽅法。我们对处理类中的所有⽅法的调用都会变成对invoke方法的调⽤。这样我们可以在invoke方法中添加统一的处理逻辑。这个中间类有一个委托类对象引⽤，在Invoke方法中调用了委托类对象的相应方法，通过这种聚合的方式持有委托类对象引⽤，把外部对invoke的调用最终都转为对委托类对象的调用。

实际上，中间类与委托类构成了了静态代理关系，在这个关系中， 中间类是代理类，委托类是委托类。然后代理类与中间类也构成⼀个静态代理关系，在这个关系中，中间类是委托类，代理类是代理类。也就是说，动态代理理关系由两组静态代理理关系组成，这就是动态代理理的原理。

### 委托类接口

```java
package com.thisxzj.proxy.invocation;


/**
 * date    2019-09-27
 * time    00:07
 *
 * @author thisxzj - 中建
 */

// 委托类接口
public interface IHelloService {

    /**
     * 方法1
     * @param userName
     * @return
     */
    String sayHello(String userName);

    /**
     * 方法2
     * @param userName
     * @return
     */
    String sayByeBye(String userName);

}
```

### 委托类

```java 
package com.thisxzj.proxy.invocation;

/**
 * date    2019-09-27
 * time    00:07
 *
 * @author thisxzj - 中建
 */

// 委托类
public class HelloService implements IHelloService {

    @Override
    public String sayHello(String userName) {
        System.out.println(userName + " hello");
        return userName + " hello";
    }

    @Override
    public String sayByeBye(String userName) {
        System.out.println(userName + " ByeBye");
        return userName + " ByeBye";
    }
}
```

### 中间类

```java
package com.thisxzj.proxy.invocation;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * date    2019-09-27
 * time    00:08
 *
 * @author thisxzj - 中建
 */


// 中间类
public class JavaProxyInvocationHandler implements InvocationHandler {

    /**
     * 中间类持有委托类对象的引用,这里会构成一种静态代理关系
     */
    private Object obj;

    /**
     * 有参构造器,传入委托类的对象
     *
     * @param obj 委托类的对象
     */
    public JavaProxyInvocationHandler(Object obj) {
        this.obj = obj;

    }

    /**
     * 动态生成代理类对象,Proxy.newProxyInstance
     *
     * @return 返回代理类的实例
     */
    public Object newProxyInstance() {
        return Proxy.newProxyInstance(
                //指定代理对象的类加载器
                obj.getClass().getClassLoader(),
                //代理对象需要实现的接口，可以同时指定多个接口
                obj.getClass().getInterfaces(),
                //方法调用的实际处理者，代理对象的方法调用都会转发到这里
                this::invoke);
    }


    /**
     * @param proxy  代理对象
     * @param method 代理方法
     * @param args   方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invoke before");
        Object result = method.invoke(obj, args);
        System.out.println("invoke after");
        return result;
    }
}
```

### 测试

```java
package com.thisxzj.proxy.invocation;

/**
 * date    2019-09-27
 * time    00:09
 *
 * @author thisxzj - 中建
 */


public class Main {
    public static void main(String[] args) {
        JavaProxyInvocationHandler proxyInvocationHandler = new JavaProxyInvocationHandler(new HelloService());
        IHelloService helloService = (IHelloService) proxyInvocationHandler.newProxyInstance();
        helloService.sayByeBye("paopao");
        helloService.sayHello("yupao");
    }
}
```

### 结果

```java
invoke before
xzj ByeBye
invoke after
invoke before
jzx hello
invoke after
```

在上面的测试动态代理类中,我们调用 Proxy 类的 newProxyInstance 方法来获取一个代理类实例。这个代理类实现了我们指定的接口并且会把方法调用分发到指定的调用处理器。

首先通过 newProxyInstance 方法获取代理类的实例,之后就可以通过这个代理类的实例调用代理类的方法，对代理类的方法调用都会调用中间类(实现了 invocationHandle的类)的invoke 方法，在 invoke 方法中我们调用委托类的对应方法，然后加上自己的处理逻辑。

java 动态代理最大的特点就是动态生成的代理类和委托类实现同一个接口。java 动态代理其实内部是通过反射机制实现的，也就是已知的一个对象，在运行的时候动态调用它的方法，并且调用的时候还可以加一些自己的逻辑在里面。

#### 总结

1. 静态代理比较容易理解,需要被代理的类和代理类实现自同一个接口,然后在代理类中调用真正实现类,并且静态代理的关系在编译期间就已经确定了。而动态代理的关系是在运行期间确定的。静态代理实现简单，适合于代理类较少且确定的情况，而动态代理则给我们提供了更大的灵活性。
2. JDK动态代理所用到的代理类在程序调用到代理类对象时才由JVM真正创建，JVM根据传进来的 业务实现类对象 以及 方法名 ，动态地创建了一个代理类的class文件并被字节码引擎执行，然后通过该代理类对象进行方法调用。我们需要做的，只需指定代理类的预处理、调用后操作即可。
3. 静态代理和动态代理都是基于接口实现的,而对于那些没有提供接口只是提供了实现类的而言,就只能选择CGLIB动态代理了
4. JDK动态代理和CGLIB动态代理的区别
   JDK动态代理基于Java反射机制实现,必须要实现了接口的业务类才能用这种方法生成代理对象
   CGLIB动态代理基于 ASM 框架通过生成业务类的子类来实现
   JDK动态代理的优势是最小化依赖关系，减少依赖意味着简化开发和维护并且有JDK自身支持。还可以平滑进行JDK版本升级，代码实现简单。基于CGLIB框架的优势是无须实现接口，达到代理类无侵入，我们只需操作我们关系的类，不必为其它相关类增加工作量，性能比较高。

# 面试

描述代理的几种实现方式? 分别说出优缺点?

代理可以分为 "静态代理" 和 "动态代理"，动态代理又分为 "JDK动态代理" 和 "CGLIB动态代理" 实现。

1. 静态代理：代理对象和实际对象都继承了同一个接口，在代理对象中指向的是实际对象的实例，这样对外暴露的是代理对象而真正调用的是 Real Object.
   1. 优点：可以很好的保护实际对象的业务逻辑对外暴露，从而提高安全性。
   2. 缺点：不同的接口要有不同的代理类实现，会很冗余。
   
2. JDK 动态代理：
   为了解决静态代理中生成大量的代理类造成的冗余。
   JDK 动态代理只需要实现 InvocationHandler 接口，重写 invoke 方法便可以完成代理的实现。
   jdk的代理是利用反射生成代理类 Proxyxx.class 代理类字节码，并生成对象。
   jdk动态代理之所以只能代理接口是因为代理类本身已经extends了Proxy，而java是不允许多重继承的，但是允许实现多个接口
   
   1. 优点：解决了静态代理中冗余的代理实现类问题。
   2. 缺点：JDK 动态代理是基于接口设计实现的，如果没有接口，会抛异常。
   
3. CGLIB 代理：
   由于 JDK 动态代理限制了只能基于接口设计，而对于没有接口的情况，JDK方式解决不了。
   CGLIb 采用了非常底层的字节码技术，其原理是通过字节码技术为一个类创建子类，并在子类中采用方法拦截的技术拦截所有父类方法的调用，顺势织入横切逻辑，来完成动态代理的实现。
   
   实现方式实现 MethodInterceptor 接口，重写 intercept方法，通过 Enhancer 类的回调方法来实现。
   
   但是CGLib在创建代理对象时所花费的时间却比JDK多得多，所以对于单例的对象，因为无需频繁创建对象，用CGLib合适，反之，使用JDK方式要更为合适一些。
   同时，由于CGLib由于是采用动态创建子类的方法，对于final方法，无法进行代理。
   
   1. 优点：没有接口也能实现动态代理，而且采用字节码增强技术，性能也不错。
   2. 缺点：技术实现相对难理解些。