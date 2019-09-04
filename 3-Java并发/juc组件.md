# CountDownLatch

对多个线程维护一个计数器，每次使用countDown的时候，计数器的数字减少1，直至为0的时候，使用await的方法就会被唤醒。

```java
package com.company.juc;

import java.util.concurrent.*;

/**
 * date    2019-09-04
 * time    11:56
 *
 * @author thisxzj - 中建
 */


public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        final int totalThread = 10;
        CountDownLatch countDownLatch = new CountDownLatch(totalThread);

        ExecutorService executorService = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Thread::new,
                new ThreadPoolExecutor.AbortPolicy());


        for (int i = 0; i < totalThread; i++) {
            executorService.execute(() -> {
                System.out.print("run..");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("end");
        executorService.shutdown();
    }
}

```

# CyclicBarrier

用来控制多个线程相互等待，只有当多个线程到达时，才会继续执行。

用来控制多个线程互相等待，只有当多个线程都到达时，这些线程才会继续执行。

​	和 CountdownLatch 相似，都是通过维护计数器来实现的。线程执行 await() 方法之后计数器会减 1，并进行等待，直到计数器为 0，所有调用 await() 方法而在等待的线程才能继续执行。

​	CyclicBarrier 和 CountdownLatch 的一个区别是，CyclicBarrier 的计数器通过调用 reset() 方法可以循环使用，所以它才叫做循环屏障。

​	CyclicBarrier 构造函数有两个参数，其中第一个参数 parties 指示计数器的初始值，第二个参数 barrierAction 在所有线程都到达屏障的时候会执行一次的动作。

```java
package com.company.juc;

/**
 * date    2019-09-04
 * time    14:48
 *
 * @author thisxzj - 中建
 */

import java.util.concurrent.CyclicBarrier;

//   @ author :zjxu     time:2019/1/6

public class CyclicBarrierMain {
    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSolider = new Thread[N];

        //第一个参数设置线程个数，第二个参数接口加载的是所有的线程都运行完毕之后，所执行的方法
        CyclicBarrier cyclicBarrier = new CyclicBarrier(N - 2, () ->
                System.out.println("任务 " + (N - 2) + " 个完成"));

        System.out.println("开始任务");
        for (int i = 0; i < N; i++) {
            allSolider[i] = new Thread(new CyclicBarrierTest(cyclicBarrier));
            allSolider[i].start();

        }
    }
}


class CyclicBarrierTest implements Runnable {
    private final CyclicBarrier cyclicBarrier;

    CyclicBarrierTest(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    void doWork() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 1000));
        System.out.println(Thread.currentThread().getName() + " 完成");
    }

    //n个线程执行的操作
    @Override
    public void run() {
        try {
            doWork();
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

​	运行结果：

```html
开始任务
Thread-8 完成
Thread-3 完成
Thread-2 完成
Thread-0 完成
Thread-7 完成
Thread-1 完成
Thread-9 完成
Thread-5 完成
任务 8 个完成
Thread-4 完成
Thread-6 完成
```

# Semaphore

​	Semaphore 类似于操作系统中的信号量，可以控制对互斥资源的访问线程数。

​	以下代码模拟了对某个服务的并发请求，每次只能有 3 个客户端同时访问，请求总数为 10。

```java
package com.company.juc;


import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * date    2019-09-04
 * time    14:50
 *
 * @author thisxzj - 中建
 */

public class SemaphoreMain {
    public static void main(String[] args) {
        SemaphoreDemo semaphoreDemo = new SemaphoreDemo();


        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();


        ExecutorService se = new ThreadPoolExecutor(
                10,
                10,
                8L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());


        se.submit(semaphoreDemo.new TaskDemo("a"));
        se.submit(semaphoreDemo.new TaskDemo("b"));
        se.submit(semaphoreDemo.new TaskDemo("c"));
        se.submit(semaphoreDemo.new TaskDemo("d"));
        se.submit(semaphoreDemo.new TaskDemo("e"));
        se.submit(semaphoreDemo.new TaskDemo("f"));
        se.shutdown();
    }
}

class SemaphoreDemo {

    private Semaphore smp = new Semaphore(3);
    private Random rnd = new Random();

    class TaskDemo implements Runnable {
        private String id;

        TaskDemo(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            int delay = rnd.nextInt(1000);
            try {
                smp.acquire();
                System.out.println(Thread.currentThread().getName() + " " + id + " is working");
                System.out.println("    " + Thread.currentThread().getName() + "  " + delay + "ms");
                Thread.sleep(delay);
                System.out.println(Thread.currentThread().getName() + " " + id + "   is  over");
                smp.release();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
```

运行结果：

```java
demo-pool-0 a is working
demo-pool-2 c is working
demo-pool-1 b is working
    demo-pool-2  71ms
    demo-pool-0  546ms
    demo-pool-1  734ms
demo-pool-2 c   is  over
demo-pool-2 d is working
    demo-pool-2  369ms
demo-pool-2 d   is  over
demo-pool-2 e is working
    demo-pool-2  733ms
demo-pool-0 a   is  over
demo-pool-0 f is working
    demo-pool-0  749ms
demo-pool-1 b   is  over
demo-pool-2 e   is  over
demo-pool-0 f   is  over
```

每次只能有三个线程进入smp.acquire中，直到有线程smp.release释放了信号。

类似于有数量限制的synchronized关键字的效果。

# FutureTask

在使用 Callable实现线程的启动的时候，我们知道它可以有返回值，返回值可以通过 Future<V> 进行封装。FutureTask 实现了 RunnableFuture 接口，该接口继承自 Runnable 和 Future<V> 接口，这使得 FutureTask 既可以当做一个任务执行，也可以有返回值。

这个过程是一个异步阻塞过程。

```java
package com.company.juc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Date;
import java.util.concurrent.*;

/**
 * date    2019-09-04
 * time    15:20
 *
 * @author thisxzj - 中建
 */


public class FutureTaskMain {
    public static void main(String[] args) throws Exception {

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

        Callable<Integer> call =
                () -> {
                    System.out.println("子线程: -- 正在计算结果 --");
                    System.out.println("子线程: -- 延迟3000ms -- 开始 -- " + new Date().toString());
                    Thread.sleep(3000);
                    System.out.println("子线程: -- 延迟3000ms --  结束 -- " + new Date().toString());
                    return (int) (Math.random() * 100);
                };
        FutureTask<Integer> task = new FutureTask<>(call);

        ExecutorService pool = new ThreadPoolExecutor(
                1,
                1,
                8L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(16),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        try {
            pool.execute(task);
        } finally {
            pool.shutdown();
        }

        System.out.println("主线程: -- 干点别的 -- " + new Date().toString());
        Thread.sleep(5000);
        Integer result = task.get();
        System.out.println("主线程: -- 拿到的结果为" + result + " -- " + new Date().toString());
    }
}
```

运行的结果是：

```java
子线程: -- 正在计算结果 --
子线程: -- 延迟3000ms -- 开始 -- Wed Sep 04 15:36:15 CST 2019
主线程: -- 干点别的 -- Wed Sep 04 15:36:15 CST 2019
子线程: -- 延迟3000ms --  结束 -- Wed Sep 04 15:36:18 CST 2019
主线程: -- 拿到的结果为38 -- Wed Sep 04 15:36:20 CST 2019
```

在提交任务时，用户实现的Callable实例task会被包装为FutureTask实例ftask，提交后任务异步执行，无需用户关心，当用户需要时，再调用futureTask.get()获取结果或异常。

但是如果在使用futureTask.get()的时候，task没有执行完毕，那么这个方法就阻塞住了，没有办法往下执行。比如：

```java
package com.company.juc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Date;
import java.util.concurrent.*;

/**
 * date    2019-09-04
 * time    15:20
 *
 * @author thisxzj - 中建
 */


public class FutureTaskMain {
    public static void main(String[] args) throws Exception {

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

        Callable<Integer> call =
                () -> {
                    System.out.println("子线程: -- 正在计算结果 --");
                    System.out.println("子线程: -- 延迟3000ms -- 开始 -- " + new Date().toString());
                    Thread.sleep(3000);
                    System.out.println("子线程: -- 延迟3000ms --  结束 -- " + new Date().toString());
                    return (int) (Math.random() * 100);
                };
        FutureTask<Integer> task = new FutureTask<>(call);

        ExecutorService pool = new ThreadPoolExecutor(
                1,
                1,
                8L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(16),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        try {
            pool.execute(task);
        } finally {
            pool.shutdown();
        }

        System.out.println("主线程: -- 干点别的 -- " + new Date().toString());
        Thread.sleep(1000);
        Integer result = task.get();
        System.out.println("主线程: -- 拿到的结果为" + result + " -- " + new Date().toString());
    }
}
```

运行结果：

```java
子线程: -- 正在计算结果 --
子线程: -- 延迟3000ms -- 开始 -- Wed Sep 04 15:44:20 CST 2019
主线程: -- 干点别的 -- Wed Sep 04 15:44:20 CST 2019
子线程: -- 延迟3000ms --  结束 -- Wed Sep 04 15:44:23 CST 2019
主线程: -- 拿到的结果为76 -- Wed Sep 04 15:44:23 CST 2019
```

所以，在get方法这里被阻塞掉了。这是一个阻塞的方法。

# BlockingQueue

这是一个接口，有以下阻塞队列的实现：

-  FIFO 队列 ：LinkedBlockingQueue、ArrayBlockingQueue（固定长度）
-  优先级队列 ：PriorityBlockingQueue

提供了阻塞的 take() 和 put() 方法：

- 如果队列为空，那么take方法将阻塞，直到队列中有内容。
- 如果队列为满put将阻塞，直到队列有空闲位置。

可以使用BlockingQueue实现生产者消费者的问题，在实现生产者消费者队列的时候，主要使用的就是他的这种阻塞的性质。

```java
package com.company.juc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * date    2019-09-04
 * time    15:48
 *
 * @author thisxzj - 中建
 */



public class BlockingQueueMain {
    public static void main(String[] args) {
        Thread consumer = new Thread(new Consumer());
        Thread producer = new Thread(new Producer());
        consumer.start();
        producer.start();
    }
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(20);


    private static class Producer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                int production = 0;
                try {
                    production = (int) (Math.random() * 100);
                    queue.put(production);
                    Thread.sleep((int) (Math.random() * 600));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("pro - :" + "  " + production);
                Thread.yield();
            }
        }
    }

    private static class Consumer implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                int production = 0;
                try {
                    production = queue.take();
                    Thread.sleep((int) (Math.random() * 800));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("con - :" + "  " + production);
            }
        }
    }
}
```

​	运行结果：

```java
con - :  6
pro - :  6
pro - :  75
con - :  75
con - :  29
pro - :  29
con - :  36
pro - :  36
con - :  73
pro - :  73
pro - :  39
con - :  39
pro - :  66
con - :  66
pro - :  88
con - :  88
con - :  2
pro - :  2
pro - :  80
con - :  80
```