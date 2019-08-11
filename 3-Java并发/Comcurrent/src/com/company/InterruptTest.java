package com.company;

/**
 * 中建
 *
 * @author thisxzj
 * @date 2019 2019-08-11 13:09
 */


public class InterruptTest {
    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        Thread t = new Thread(() -> {
            //判断是否中断
            try {
                System.out.println("sleep之前的检测 " + Thread.interrupted());
                Thread.sleep(500);
                System.out.println(System.currentTimeMillis() - time);
            } catch (InterruptedException e) {
                System.out.println(System.currentTimeMillis() - time + "抛出中断异常");
            }
            System.out.println(" 线程结束部分 " + Thread.currentThread().isInterrupted());

        });
        t.start();

        Thread.sleep(600);
        t.interrupt();
        System.out.println("main中的设置方法" + t.isInterrupted());
    }
}
