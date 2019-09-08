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
            for (int i = 0; i < 5; i++) {
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