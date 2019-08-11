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
