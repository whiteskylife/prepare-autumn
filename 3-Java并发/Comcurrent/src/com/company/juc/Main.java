package com.company.juc;

/**
 * date    2019-09-04
 * time    13:58
 *
 * @author thisxzj - 中建
 */

import java.util.concurrent.CyclicBarrier;

//   @ author :zjxu     time:2019/1/6

public class Main {
    public static void main(String[] args) {
        final int N = 10;
        Thread[] allSolider = new Thread[N];

        //第一个参数设置线程个数，第二个参数接口加载的是所有的线程都运行完毕之后，所执行的方法
        CyclicBarrier cyclicBarrier = new CyclicBarrier(N - 2, () ->
                System.out.println("任务 " + (N - 2) + " 个完成"));

        System.out.println("开始任务");
        for (int i = 0; i < N; i++) {
            allSolider[i] = new Thread(new CyclicBarrierTest(cyclicBarrier));
            allSolider[i].start();

        }
    }
}


class CyclicBarrierTest implements Runnable {
    private final CyclicBarrier cyclicBarrier;

    CyclicBarrierTest(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    void doWork() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 1000));
        System.out.println(Thread.currentThread().getName() + " 完成");
    }

    //n个线程执行的操作
    @Override
    public void run() {
        try {
            doWork();
            cyclicBarrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

