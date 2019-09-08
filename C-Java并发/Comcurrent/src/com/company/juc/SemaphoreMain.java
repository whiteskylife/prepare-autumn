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