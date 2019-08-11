package com.company;

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

        new Thread(() -> {
            synchronized (lock) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            synchronized (lock) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.notify();
            }
        }).start();
    }
}

