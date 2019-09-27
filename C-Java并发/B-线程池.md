# 线程池

线程池：Executor 管理多个异步任务的执行，而无需程序员显式地管理线程的生命周期。这里的异步是指多个任务的执行互不干扰，不需要进行同步操作。

主要有四种 Executor：

## cachedThreadPool

​	newCachedThreadPool创建一**个可缓**存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。

​	下面是它的使用方法：

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            cachedThreadPool.execute(new MyRunnable());
        }
        cachedThreadPool.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
```



## fixedThreadPool

​	newFixedThreadPool可以创建一个定长的线程池。定长线程池最多只能同时执行一定个数的线程，这个容量在new的时候设定。

​	下面是一个示例：

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            cachedThreadPool.execute(new MyRunnable());
        }
        cachedThreadPool.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
```

运行结果：

```java
Sat Jan 05 11:59:13 CST 2019
Sat Jan 05 11:59:13 CST 2019
Sat Jan 05 11:59:13 CST 2019
Sat Jan 05 11:59:15 CST 2019
Sat Jan 05 11:59:15 CST 2019
Sat Jan 05 11:59:15 CST 2019
Sat Jan 05 11:59:17 CST 2019
Sat Jan 05 11:59:17 CST 2019
Sat Jan 05 11:59:17 CST 2019
Sat Jan 05 11:59:19 CST 2019
```



## scheduledThreadPool

​	newScheduledThreadPool创建一个定长线程池，支持延迟执行和周期性任务执行。后一种执行方式类似于单片机的定时器中断。

### 延迟执行

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        System.out.println(new Date().toString());
        scheduledThreadPool.schedule(new MyRunnable(), 3, TimeUnit.SECONDS);
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
    }
}
```

​	结果：

```java
Sat Jan 05 12:15:35 CST 2019
Sat Jan 05 12:15:38 CST 2019
```

### 	定时执行

​	scheduleAtFixedRate(x,x,x,x)的第二个参数的意义是初始化延迟，第三个参数的意义是定时间隔延迟。

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        System.out.println(new Date().toString());
        scheduledThreadPool.scheduleAtFixedRate(new MyRunnable(), 1, 3, TimeUnit.SECONDS);
        try {
            Thread.sleep(15 * 1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        scheduledThreadPool.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
    }
}
```

结果

```java
Sat Jan 05 12:21:07 CST 2019
Sat Jan 05 12:21:08 CST 2019
Sat Jan 05 12:21:11 CST 2019
Sat Jan 05 12:21:14 CST 2019
Sat Jan 05 12:21:17 CST 2019
Sat Jan 05 12:21:20 CST 2019
```

​	结果中第一二行的原因就是初始化的延迟造成的。

## singleThreadExecutor

​	newSingleThreadExecutor创建一个单线程化的线程池，这个线程池当前池中的线程死后(或发生异常时)，才能重新启动新的一个线程来替代原来的线程继续执行下去。也就是说按照单线程的模式，会按照线程添加的顺序，一个一个的执行这些线程的工作。

```java
package com.xzj;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            singleThreadExecutor.execute(new MyRunnable(index));
        }
    }
}

class MyRunnable implements Runnable {
    int i = 0;

    public MyRunnable(int i) {
        this.i = i;
    }

    @Override
    public void run() {
        try {
            System.out.print(i+" ");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

运行的结果

```java
0 1 2 3 4 5 6 7 8 9 
```



# Executor 的中断操作

调用 Executor 的 shutdown() 方法会等待线程都执行完毕之后再关闭，但是如果调用的是 shutdownNow() 方法，则相当于调用每个线程的 interrupt() 方法。

以下使用 Lambda 创建线程，相当于创建了一个匿名内部线程。

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        long beginTime = System.currentTimeMillis();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        scheduledThreadPool.schedule(new MyRunnable(), 5, TimeUnit.SECONDS);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        System.out.println("中断时间：" + (System.currentTimeMillis() - beginTime));
        scheduledThreadPool.shutdownNow();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println(new Date().toString());
    }
}
```

运行结果：

```java
中断时间：3007
```

如果只想中断 Executor 中的一个线程，可以通过使用 submit() 方法来提交一个线程，它会返回一个 Future<?> 对象，通过调用该对象的 cancel(true) 方法就可以中断线程。

​	例如：在这个程序中，我们想要中断编号为 5 的线程。我们将编号为 5 的线程筛选出来 ， 使用future.cancel(true);的语句将其中断。

```java
package com.xzj;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//   @ author :zjxu     time:2019/1/5
public class Main {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        System.out.println("Begin time:\n" + new Date().toString() + "\n");

        Future<?> future = null;
        for (int i = 0; i < 10; i++) {
            if (i != 5) {
                cachedThreadPool.submit(new MyRunnable(i));
            } else {
                future = cachedThreadPool.submit(new MyRunnable(i));
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        if (future != null)
            future.cancel(true);
    }
}

class MyRunnable implements Runnable {
    int index = 0;

    public MyRunnable(int index) {
        this.index = index;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        System.out.println("" + index + "       " + new Date().toString());
    }

}
```

运行结果显示：

```java
Begin time:
Sat Jan 05 14:56:44 CST 2019

java.lang.InterruptedException: sleep interrupted
	at java.base/java.lang.Thread.sleep(Native Method)
	at com.xzj.MyRunnable.run(Main.java:42)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
	at java.base/java.lang.Thread.run(Thread.java:834)
5       Sat Jan 05 14:56:46 CST 2019
0       Sat Jan 05 14:56:49 CST 2019
7       Sat Jan 05 14:56:49 CST 2019
6       Sat Jan 05 14:56:49 CST 2019
4       Sat Jan 05 14:56:49 CST 2019
3       Sat Jan 05 14:56:49 CST 2019
1       Sat Jan 05 14:56:49 CST 2019
2       Sat Jan 05 14:56:49 CST 2019
8       Sat Jan 05 14:56:49 CST 2019
9       Sat Jan 05 14:56:49 CST 2019
```

​	在 5 号线程sleep的时候，我们使用了中断函数，将其中断。此时5号线程报错，然后异常退出sleep状态。5号线程只有离我们程序的开始时间只有两秒的间隔，但是其余的线程均是五秒结束，这说明了，中断方法在两秒的时候发挥了左右。

# 推荐的线程池构建方法

```java
	 public ThreadPoolExecutor(int corePoolSize, // 1
                              int maximumPoolSize,  // 2
                              long keepAliveTime,  // 3
                              TimeUnit unit,  // 4
                              BlockingQueue<Runnable> workQueue, // 5
                              ThreadFactory threadFactory,  // 6
                              RejectedExecutionHandler handler ) { //7
     
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```

| 序号 | 名称            | 类型                     | 含义             |
| ---- | --------------- | ------------------------ | ---------------- |
| 1    | corePoolSize    | int                      | 核心线程池大小   |
| 2    | maximumPoolSize | int                      | 最大线程池大小   |
| 3    | keepAliveTime   | long                     | 线程最大空闲时间 |
| 4    | unit            | TimeUnit                 | 时间单位         |
| 5    | workQueue       | BlockingQueue<Runnable>  | 线程等待队列     |
| 6    | threadFactory   | ThreadFactory            | 线程创建工厂     |
| 7    | handler         | RejectedExecutionHandler | 拒绝策略         |

上面说到的所有线程池都是使用不同的上面的参数的组合完成的。

## cachedThreadPool

```java
     public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, 
                                      Integer.MAX_VALUE,
                                      60L, 
                                      TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }
```

1. corePoolSize = 0，maximumPoolSize = Integer.MAX_VALUE，即线程数量几乎无限制；
2. keepAliveTime = 60s，线程空闲60s后自动结束。
3. workQueue为SynchronousQueue同步队列，使用SynchronousQueue的目的就是保证“对于提交的任务，如果有空闲线程，则使用空闲线程来处理；否则新建一个线程来处理任务”。
4. 适用场景：快速处理大量耗时较短的任务，如Netty的NIO接受请求时，可使用。



## fixedThreadPool

```java
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, 
                                      nThreads,
                                      0L, 
                                      TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }
```

1. corePoolSize与maximumPoolSize相等，即其线程全为核心线程，是一个固定大小的线程池，是其优势。
2. keepAliveTime = 0 该参数默认对核心线程无效，而FixedThreadPool全部为核心线程。
3. workQueue 为LinkedBlockingQueue，队列最大值为Integer.MAX_VALUE。如果任务提交速度持续大余任务处理速度，会造成队列大量阻塞。因为队列很大，很有可能在拒绝策略前，内存溢出。是其劣势。
4. FixedThreadPool的任务执行是无序的。
5. 适用场景：可用于Web服务瞬时削峰，但需注意长时间持续高峰情况造成的队列阻塞。

## singleThreadExecutor

```java
    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
```

1. 这里多了一层FinalizableDelegatedExecutorService包装，使其无法成功向下转型。
2. 其余的性质和fixedThreadPool相同。
