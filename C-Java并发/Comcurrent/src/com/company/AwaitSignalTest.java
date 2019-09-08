package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 21:38
 */


public class AwaitSignalTest {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    void before() {
        lock.lock();
        try {
            System.out.println("before");
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void after() {
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

class AwaitSignalMain {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        AwaitSignalTest example = new AwaitSignalTest();
        executorService.execute(example::after);
        executorService.execute(example::before);
        executorService.shutdown();
    }
}