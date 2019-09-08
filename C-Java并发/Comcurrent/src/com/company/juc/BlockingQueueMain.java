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

