
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
            executorService.execute(new MyRunnableInWait());
        }
        executorService.shutdown();
    }
}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        SynchronizedExampleInWait s = new SynchronizedExampleInWait();
        try {
            s.func();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class SynchronizedExample {
    public void func() throws InterruptedException {
        synchronized (SynchronizedExampleInWait.class) {
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




/*

 */