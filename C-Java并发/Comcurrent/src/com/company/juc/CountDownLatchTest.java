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
                0,
                Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                Thread::new,
                new ThreadPoolExecutor.AbortPolicy());


        for (int i = 0; i < totalThread; i++) {
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getThreadGroup().getName() + " - run..");
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        System.out.println("end");
        executorService.shutdown();
    }
}
