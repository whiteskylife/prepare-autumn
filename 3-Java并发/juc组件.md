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



