package com.company.juc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Date;
import java.util.concurrent.*;

/**
 * date    2019-09-04
 * time    15:20
 *
 * @author thisxzj - 中建
 */


public class FutureTaskMain {
    public static void main(String[] args) throws Exception {

        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

        Callable<Integer> call =
                () -> {
                    System.out.println("子线程: -- 正在计算结果 --");
                    System.out.println("子线程: -- 延迟3000ms -- 开始 -- " + new Date().toString());
                    Thread.sleep(3000);
                    System.out.println("子线程: -- 延迟3000ms --  结束 -- " + new Date().toString());
                    return (int) (Math.random() * 100);
                };
        FutureTask<Integer> task = new FutureTask<>(call);

        ExecutorService pool = new ThreadPoolExecutor(
                1,
                1,
                8L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(16),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        try {
            pool.execute(task);
        } finally {
            pool.shutdown();
        }

        System.out.println("主线程: -- 干点别的 -- " + new Date().toString());
        Thread.sleep(1000);
        Integer result = task.get();
        System.out.println("主线程: -- 拿到的结果为" + result + " -- " + new Date().toString());
    }
}