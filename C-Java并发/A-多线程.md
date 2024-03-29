# 进程和线程

## 进程

进程是程序的一次执行过程，是系统运行程序的基本单位。

## 线程

线程与进程相似，但线程是一个比进程更小的执行单位。一个进程在其执行的过程中可以产生多个线程。与进程不同的是同类的多个线程共享同一块内存空间和一组系统资源，所以系统在产生一个线程，或是在各个线程之间作切换工作时，负担要比进程小得多，也正因为如此，线程也被称为轻量级进程。

## 多线程

多线程就是多个线程同时运行或交替运行，通常指的是同一个进程中。

单核CPU的话是顺序执行，也就是交替运行。

多核CPU的话，因为每个CPU有自己的运算器，所以可以在多个CPU中可以同时运行。

多线程可以可以大大提高系统整体的并发能力以及性能。

线程间切换的成本远小于进程。

## 线程状态转换

![](./photo/线程状态图.png)



## 新建状态（New）

新创建了一个线程对象。

## 就绪状态（Runnable）

线程对象创建后，其他线程调用了该对象的start()方法。该状态的线程位于可运行线程池中，变得可运行，等待获取CPU的使用权。

## 运行状态（Running）

就绪状态的线程获取了CPU，执行程序代码。

## 阻塞状态（Blocked）

阻塞状态是线程因为某种原因放弃CPU使用权，暂时停止运行。直到线程进入就绪状态，才有机会转到运行状态。阻塞的情况分三种：

1. 等待阻塞：运行的线程执行wait()方法，JVM会把该线程放入等待池中。
   - wait会释放持有的锁
2. 同步阻塞：运行的线程在获取对象的同步锁时，若该同步锁被别的线程占用，则JVM会把该线程放入锁池中。
3. 其他阻塞：运行的线程执行sleep()或join()方法，或者发出了I/O请求时，JVM会把该线程置为阻塞状态。当sleep()状态超时、join()等待线程终止或者超时、或者I/O处理完毕时，线程重新转入就绪状态，然后再进入运行状态。
   - sleep不会释放持有的锁

从阻塞状态返回到可运行状态，然后再进入运行状态。

## 死亡状态（Dead）

线程执行完了或者因异常退出了run()方法，该线程结束生命周期。

# 使用线程

有四种使用线程的方法：

- 实现 Runnable 接口。
- 实现 Callable 接口。
- 继承 Thread 类。

实现 Runnable 和 Callable 接口的类只能当做一个可以在线程中运行的任务，不是真正意义上的线程，因此最后还需要通过 Thread 来调用。可以说任务是通过线程驱动从而执行的。

## 实现 Runnable 接口

需要实现 run() 方法。

通过 Thread 调用 start() 方法来启动线程。

```java
package com.xzj;

import java.util.Date;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        Runnable myRunnable = new MyRunnable();
        Thread thread = new Thread(myRunnable);
        thread.start();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
    }
}
```

```java
Sat Jan 05 11:42:04 CST 2019
```

## 实现 Callable 接口

与 Runnable 相比，Callable 可以有返回值，返回值通过 FutureTask 进行封装。

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

//   @ author :zjxu     time:2019/1/4
public class Main {
    public static void main(String[] args) throws Exception {
        MyCallable myCallable = new MyCallable();
        FutureTask<String> futureTask = new FutureTask(myCallable);
        Thread thread = new Thread(futureTask);

        thread.start();
        System.out.println(futureTask.get());
    }
}

class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return new Date().toString();
    }
}
```

```java
Sat Jan 05 11:36:11 CST 2019
```

## 继承 Thread 类

同样也是需要实现 run() 方法，因为 Thread 类也实现了 Runable 接口。

当调用 start() 方法启动一个线程时，虚拟机会将该线程放入就绪队列中等待被调度，当一个线程被调度时会执行该线程的 run() 方法。

```java
package com.xzj;

import java.util.Date;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        Thread thread = new MyThread();
        thread.start();
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println(new Date().toString());
    }
}
```

```java
Sat Jan 05 11:40:30 CST 2019
```

实现接口会更好一些，因为：

- Java 不支持多重继承，因此继承了 Thread 类就无法继承其它类，但是可以实现多个接口；
- 类可能只要求可执行就行，继承整个 Thread 类开销过大。

## 使用线程池

Executor 管理多个异步任务的执行，而无需程序员显式地管理线程的生命周期。这里的异步是指多个任务的执行互不干扰，不需要进行同步操作。

主要有三种 Executor：

- CachedThreadPool：每个任务创建一个线程。
- FixedThreadPool：一个在构造时固定大小的线程池，所有任务只能使用固定大小的线程。
- SingleThreadExecutor：相当于大小为 1 的 FixedThreadPool。如果向提交多个任务，这些任务将排队。从输出结果可以看到，任务按照提交顺序被执行

```java
public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    for (int i = 0; i < 5; i++) {
        executorService.execute(new MyRunnable());
    }
    executorService.shutdown();
}
```

# 中断

在线程阻塞、等待的时候，如果尝试中断这个线程，那么会出现中断异常，将会提前结束这个线程的阻塞或等待。IO阻塞、synchronized阻塞无法被中断。

对于中断，有三个常用的方法：

1. interrupt()

   这是Thread的实例方法，将某个线程设置成中断状态。

2. isInterrupted()

   这也是Thread的实例方法，获取某个线程的中断状态。

3. Thread.interrupted()

   这是Thread的静态方法，获取当前线程的中断状态，然后将其设置成false。

```java

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 13:09
 */


public class InterruptTest {
    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        Thread t = new Thread(() -> {
            //判断是否中断
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                    System.out.print(System.currentTimeMillis() - time);
                } catch (InterruptedException e) {
                    System.out.print("抛出中断异常  ");
                }
                System.out.println(" " + Thread.currentThread().isInterrupted());
            }
        });
        t.start();

        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000);
            System.out.print("设置为中断状态  ");
            t.interrupt();
            System.out.println(t.isInterrupted());
        }
    }
}
```

```java
562 false
1065 false
1570 false
设置为中断状态  true
抛出中断异常   false
2564 false
3065 false
3568 false
设置为中断状态  true
抛出中断异常   false
4568 false
5068 false
5569 false
设置为中断状态  true
抛出中断异常   false
6573 false
7077 false
7577 false
8082 false
8585 false
```



```java
/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 13:09
 */


public class InterruptTest {
    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        Thread t = new Thread(() -> {
            //判断是否中断
            try {
                System.out.println("sleep之前的检测 " + Thread.interrupted());
                Thread.sleep(500);
                System.out.println(System.currentTimeMillis() - time);
            } catch (InterruptedException e) {
                System.out.println(System.currentTimeMillis() - time + "抛出中断异常");
            }
            System.out.println(" 线程结束部分 " + Thread.currentThread().isInterrupted());

        });
        t.start();
        
        Thread.sleep(600);
        t.interrupt();
        System.out.println("main中的设置方法" + t.isInterrupted());
    }
}
```

```java
sleep之前的检测 false
568
 线程结束部分 false
main中的设置方法false
```

通过上面的两个例子说明，在阻塞状态下使用线程的interrupt()的方法，并不是将线程直接结束，而是不再执行try部分sleep方法之后的部分，然后继续执行try-catch部分之后的代码。



