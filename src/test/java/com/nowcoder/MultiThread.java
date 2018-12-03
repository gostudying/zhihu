package com.nowcoder;

import java.util.concurrent.Callable;

class MyThread extends Thread {

	// 给线程取名
	public MyThread(String name) {
		super(name);
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; ++i) {
			try {
				sleep(300);
				System.out.println(getName() + "===" + i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

// 这种实现将任务和线程分开
class MyTask implements Runnable {

	@Override
	public void run() {
		for (int i = 0; i < 10; ++i) {
			try {
				Thread.sleep(300);
				System.out.println(Thread.currentThread().getName() + "===" + i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}

class MyCallable implements Callable<Object> {

	@Override
	public Object call() throws Exception {
		for (int i = 0; i < 10; ++i) {
			Thread.sleep(300);
			System.out.println(Thread.currentThread().getName() + "===" + i);
		}
		return "over";
	}
}

class Add implements Callable<Integer> {
	private int a;
	private int b;

	public Add(int a, int b) {
		super();
		this.a = a;
		this.b = b;
	}

	@Override
	public Integer call() throws Exception {
		return a + b;
	}

}

public class MultiThread {

	public static void main(String[] args) throws Exception {
		// MyThread t1 = new MyThread("线程1");
		// t1.start();
		// for (int i = 0; i < 10; ++i) {
		// try {
		// Thread.sleep(300);
		// System.out.println(Thread.currentThread().getName() + "===" + i);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }

		// MyTask task = new MyTask();
		// Thread t1 = new Thread(task);
		// Thread t2 = new Thread(task);
		// t1.start();
		// t2.start();

		// new Thread(){
		// public void run() {
		// for (int i = 0; i < 10; ++i) {
		// try {
		// Thread.sleep(300);
		// System.out.println(Thread.currentThread().getName() + "===" + i);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// };
		// }.start();

		// Runnable runnable = new Runnable() {
		// public void run() {
		// for (int i = 0; i < 10; ++i) {
		// try {
		// Thread.sleep(300);
		// System.out.println(Thread.currentThread().getName() + "===" + i);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// };
		//
		// new Thread(runnable).start();

		// ExecutorService pool = Executors.newFixedThreadPool(10);
		// MyTask task = new MyTask();
		// pool.submit(task);
		// pool.submit(task);
		// pool.submit(task);
		// //关闭线程池
		// pool.shutdown();

		// ExecutorService pool = Executors.newFixedThreadPool(10);
		// MyCallable c = new MyCallable();
		// pool.submit(c);
		// pool.submit(c);
		// pool.submit(c);

		// ExecutorService pool = Executors.newFixedThreadPool(10);
		// Add add1 = new Add(10, 10);
		// Add add2 = new Add(100, 10);
		// Add add3 = new Add(1000, 10);
		// Future<Integer> future1 = pool.submit(add1);
		// Future<Integer> future2 = pool.submit(add2);
		// Future<Integer> future3 = pool.submit(add3);
		//
		// Integer integer1 = future1.get();
		// Integer integer2 = future2.get();
		// Integer integer3 = future3.get();
		//
		// System.out.println(integer1);
		// System.out.println(integer2);
		// System.out.println(integer3);
		//
		// pool.shutdown();

	}
}
