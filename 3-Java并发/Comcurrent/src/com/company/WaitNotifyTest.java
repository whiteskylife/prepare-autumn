package com.company;

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

