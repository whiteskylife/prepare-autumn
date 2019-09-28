# 概念

## 同步和异步

同步和异步关注的是消息通信机制，由被调用的方法返回的信息的机制。

### 同步

同步，就是在发出一个调用时，在没有得到结果之前，该调用就不返回。但是一旦调用返回，就得到返回值了。

### 异步

异步则是相反，调用在发出之后，这个调用就直接返回了，所以没有返回结果。换句话说，当一个异步过程调用发出后，调用者不会立刻得到结果。而是在调用发出后，被调用者通过状态、通知来通知调用者，或通过回调函数处理这个调用。

## 串行和并行

### 串行

任务一个接一个的被完成，同时只能执行一个任务，任务的执行是于先后顺序的。

类似于

### 并行

任务同时执行。没有先后顺序，同时开始，如果任务量相同，也近乎同时完成。

类似于跑步比赛。

## 阻塞和非阻塞

阻塞和非阻塞关注的是程序在等待调用结果时的状态：方法调用者的状态。

### 阻塞

阻塞调用是指调用结果返回之前，当前线程会被挂起。调用线程只有在得到结果之后才会返回。

### 非阻塞

非阻塞调用指在不能立刻得到结果之前，该调用不会阻塞当前线程。

# 并发基础

## daemon

daemon是是守护线程的意思，当所有的非守护线程结束的时候，整个线程结束，也就是说守护线程是为非守护线程服务的。

此外main方法是非守护线程，也属于被服务对象。

调用方法是：

```java
public static void main(String[] args) {
    Thread thread = new Thread(new MyRunnable());
    thread.setDaemon(true);
}
```

## yield

yield方法是Thread的静态方法，表示在线程调度上让步，通常在一个线程的主要任务完成之后使用这个方法。该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。

```java
public void run() {
    Thread.yield();
}
```

## join

join()的作用是：“等待该线程终止”。这里需要理解的就是该线程是指的主线程等待子线程的终止。也就是在子线程调用了join()方法后面的代码，只有等到子线程结束了才能执行。

```java
Thread t = new AThread(); 
t.start(); 
t.join(); //表示需要等待t线程的结束，这个线程(通常是main)才能继续执行下去。
```

join的意思就是，执行的`t.join()`语句的线程，需要等待t线程结束。

## sleep

Thread.sleep(millisec) 方法会休眠当前正在执行的线程，millisec 单位为毫秒。

sleep() 可能会抛出 InterruptedException，因为异常不能跨线程传播回 main() 中，因此必须在本地进行处理。线程中抛出的其它异常也同样需要在本地进行处理。

```java
public void run() {
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```



# 互斥同步

## Synchronized

### synchronized (X.class)

```java
package com.company;

import java.util.concurrent.*;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 14:27
 */


public class SynchronizedTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            executorService.execute(new MyRunnable());
        }
        executorService.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        SynchronizedExample s = new SynchronizedExample();
        try {
            s.func();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class SynchronizedExample {
    public void func() throws InterruptedException {
        synchronized (SynchronizedExample.class) {
            for (int i = 0; i < 5; i++) {
                long threadId = Thread.currentThread().getId();
                System.out.println(threadId + " " + i + " ");
                if (threadId % 2 == 0) {
                    Thread.sleep(2);
                }
            }
        }
    }
}
```

结果

```Java
10 0 
10 1 
10 2 
10 3 
10 4 
12 0 
12 1 
12 2 
12 3 
12 4 
11 0 
11 1 
11 2 
11 3 
11 4 
```

如果将同步语句去掉

```java
package com.company;

import java.util.concurrent.*;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 14:27
 */


public class SynchronizedTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            executorService.execute(new MyRunnable());
        }
        executorService.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        SynchronizedExample s = new SynchronizedExample();
        try {
            s.func();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class SynchronizedExample {
    public void func() throws InterruptedException {
        //使用this表示类的实例只能有一个执行这个代码块方法
//        synchronized (SynchronizedExample.class) {
        for (int i = 0; i < 5; i++) {
            long threadId = Thread.currentThread().getId();
            System.out.println(threadId + " " + i + " ");
            if (threadId % 2 == 0) {
                Thread.sleep(2);
            }
        }
//        }
    }
}
```

```java
11 0 
11 1 
11 2 
11 3 
11 4 
10 0 
12 0 
10 1 
12 1 
10 2 
12 2 
10 3 
12 3 
10 4 
12 4 
```

那么，明显结果会呈现没有顺序的执行。

### synchronized (this)

对于多个线程使用同一个实例而言，每次只能一个线程可以使用这个对象的代码块。

```java
import java.util.concurrent.*;

/**
 * @author thisxzj
 */

public class SynchronizedTest {
    public static void main(String[] args) {
        SynchronizedExample e = new SynchronizedExample();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(e::func);
        executorService.execute(e::func);
    }
}

class SynchronizedExample {
    void func() {
        synchronized (this) {
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
                try {
                    if (i % 2 == 0) {
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

```

```java
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 
```

在上面这个例子中，线程池中的两个方法都会使用func这个方法，但是只能有一个线程能够进入代码块，另一个线程只能等待前一个线程结束。

```java
package com.company;

import java.util.concurrent.*;

/**
 * @author thisxzj
 */

public class SynchronizedTest {
    public static void main(String[] args) {
        SynchronizedExample e = new SynchronizedExample();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(e::func);
        executorService.execute(e::func);
        executorService.shutdown();
    }
}

class SynchronizedExample {
    void func() {
//        synchronized (this) {
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
            try {
                if (i % 2 == 0) {
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
//    }
}
```

```java
0 0 1 2 1 2 3 4 3 4 5 6 5 6 7 8 7 8 9 9 
```

在上面这个例子中，由于没有使用同步机制，所以明显可以看出，两个线程同时都在循环体中执行这个代码块。

### 方法签名

#### 实例方法

使用在实例方法签名上的synchronized关键字，和synchronized (this)一样，作用于实例。

```Java
class SynchronizedExample {
    void func() {
        synchronized (this) {
            //TODO
        }
    }

    synchronized void func() {
        //TODO
    }
}
```

也就是说以上的两个方法等价，两种方法等价的条件是第一种写法的第二行和第三行之间不能有语句，第五行和第六行之间也不能有语句。

#### 静态方法

```java
class SynchronizedExample {
    public static void func() throws InterruptedException {
        synchronized (SynchronizedExample.class) {
            for (int i = 0; i < 5; i++) {
                long threadId = Thread.currentThread().getId();
                System.out.println(threadId + " " + i + " ");
                if (threadId % 2 == 0) {
                    Thread.sleep(2);
                }
            }
        }
    }

    static synchronized void func() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            long threadId = Thread.currentThread().getId();
            System.out.println(threadId + " " + i + " ");
            if (threadId % 2 == 0) {
                Thread.sleep(2);
            }
        }
    }
}
```

上面代码中，两个方法能够达到的同步效果是一致的，即，同时只能有一处进入这个代码块或者是方法。但是区别在于两个方法，一个是静态方法，一个是实例方法。也就是说，在使用的过程中有差异。

## Lock

锁是用于通过多个线程控制对共享资源的访问的工具。通常，锁提供对共享资源的独占访问：一次只能有一个线程可以获取锁，并且对共享资源的所有访问都要求首先获取锁。但是，一些锁可能允许并发访问共享资源，如ReentrantLock、ReadWriteLock。

### 可重入锁

ReentrantLock对资源进行加锁,同一时刻只会有一个线程能够占有锁，当前锁被线程占有时，其他线程会进入挂起状态，直到该锁被释放，其他挂起的线程会被唤醒并开始新的竞争。

从名字上理解，ReenTrantLock的字面意思就是再进入的锁，其实synchronized关键字所使用的锁也是可重入的，两者关于这个的区别不大。两者都是同一个线程每进入一次，锁的计数器都自增1，所以要等到锁的计数器下降为0时才能释放锁。



```java
package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 20:06
 */
public class ReentrantLockTest {
    private Lock lock = new ReentrantLock();

    void func() {
        lock.lock();
        try {
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
            }
        } finally {
            lock.unlock(); // 确保释放锁，从而避免发生死锁。
        }
    }
}

class ReentrantLockMain {
    public static void main(String[] args) {
        ReentrantLockTest lockExample = new ReentrantLockTest();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(lockExample::func);
        executorService.execute(lockExample::func);

        executorService.shutdown();
    }
}
```

### 读写锁

读写锁是一种特殊的自旋锁，它把对共享资源对访问者划分成了读者和写者：读者只对共享资源进行访问，写者则是对共享资源进行写操作。读写锁在ReentrantLock上进行了拓展使得该锁更适合读操作远远大于写操作对场景。一个读写锁同时只能存在一个写锁但是可以存在多个读锁，但不能同时存在写锁和读锁。

```java
package com.company;


import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 20:54
 */

public class ReentrantReadWriteTest {

    public static void main(String[] args) {
        final MyObject myObject = new MyObject();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 3; j++) {
                    try {
                        myObject.put(new Random().nextInt(1000));//写操作
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        for (int i = 0; i < 3; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 3; j++) {
                    try {
                        myObject.get();//多个线程读取操作
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executorService.shutdown();
    }
}

class MyObject {
    private Object object;

    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public void get() throws InterruptedException {
        lock.readLock().lock();//上读锁
        try {
            System.out.println(Thread.currentThread().getName() + "准备读取数据");
            Thread.sleep(new Random().nextInt(1000));
            System.out.println(Thread.currentThread().getName() + "读数据为：" + this.object);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(Object object) throws InterruptedException {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "准备写数据");
            Thread.sleep(new Random().nextInt(1000));
            this.object = object;
            System.out.println(Thread.currentThread().getName() + "写数据为" + this.object);
        } finally {
            lock.writeLock().unlock();
        }
    }
}

```

## await、signal、signalAll

juc类库中提供了Condition类来实现线程之间的协调，可以在Condition上调用await方法使线程等待，其它线程调用signal或signalAll方法唤醒等待的线程。

相比于wait这种等待方式，await可以指定等待的条件，因此更加灵活。

使用Lock来获取一个Condition对象。

```java
public class AwaitSignalExample {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void before() {
        lock.lock();
        try {
            System.out.println("before");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void after() {
        lock.lock();
        try {
            condition.await();
            System.out.println("after");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
```

```java
public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    AwaitSignalExample example = new AwaitSignalExample();
    executorService.execute(() -> example.after());
    executorService.execute(() -> example.before());
}
```

```html
before
after
```




## wait、notify、notifyAll

1. wait和notify它们是Object的方法。
2. 这一组信号一般和synchronized关键字组合使用。
3. 调用wait方法时，线程进入等待状态，并且会释放synchronized的锁。
4. 一个线程使用了wait的方法之后，重新苏醒条件是：其他的线程对这个锁使用了notify方法，并且这个锁没有被占用。
5. 得到了notify信号之后，线程从执行wait的地方恢复执行。
6. 添加参数的wait和notify就是表示有时间的延迟的wait和notify。
7. notifyAll则是表示对所有在这个锁上等待的线程发起通知，然后这些线程竞争，最后一个线程获取锁，进入运行状态。

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 22:18
 */


public class WaitNotifyTest {
    public static void main(String[] args) {
        final Object lock = new Object();
        ExecutorService executorService = Executors.newCachedThreadPool();
        //等待中的线程 - A
        executorService.execute(() -> {
            synchronized (lock) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("A进入等待");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("A结束等待");
            }
        });
        //唤醒 - 等待中的线程 - B
        executorService.execute(() -> {
            synchronized (lock) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("B唤醒 - A的等待");
                lock.notify();
            }
        });
        executorService.shutdown();
    }
}


```

```java
A进入等待
B唤醒 - A的等待
A结束等待
```

1. 由同一个lock对象调用wait、notify方法，和 synchronized 锁。  
2. wait、nofity调用时必须加 synchronized(lock) 同步
3. 当线程A执行wait方法时，该线程会被挂起，阻塞。同时会释放对象锁，这就能解释上面的例子不会发生死锁。
4. 当线程B执行notify方法时，会唤醒一个被挂起的线程A。

lock对象、线程A和线程B三者是一种什么关系？根据上面的结论，可以想象一个场景：

1. lock对象维护了一个等待队列list。
2. 线程A中执行lock的wait方法，把线程A保存到lock锁的阻塞队列中。
3. 线程B中执行lock的notify方法，从lock锁的等待队列中取出线程A继续执行，若有多条线程被lock锁阻塞，则会随机唤醒一条线程继续执行。





## 比较

### 实现 

synchronized是JVM 实现的，而ReentrantLock是JDK实现的。

### 性能

新版本Java对synchronized进行了很多优化，例如自旋锁等，synchronized与ReentrantLock大致相同。

### 中断

当持有锁的线程长期不释放锁的时候，正在等待的线程可以选择放弃等待，改为处理其他事情，ReentrantLock可中断，而synchronized不行。

### 公平锁

公平锁是指多个线程在等待同一个锁时，按照申请锁的时间顺序来依次获得锁。synchronized中的锁是非公平的，ReentrantLock 默认情况下也是非公平的，但是也可以是公平的。

### 多个条件

一个 ReentrantLock 可以同时绑定多个 Condition 对象。而synchronized只能随机通知一个，或者全部通知，使用Condition的ReentrantLock则分组唤醒需要唤醒的线程们。

## 使用选择

除非需要使用ReentrantLock的高级功能，否则优先使用synchronized。这是因为synchronized是 JVM实现的一种锁机制，JVM 原生地支持它，而 ReentrantLock不是所有的JDK版本都支持。并且使用synchronized不用担心没有释放锁而导致死锁问题，因为JVM会确保锁的释放。

condition更加灵活。

### 