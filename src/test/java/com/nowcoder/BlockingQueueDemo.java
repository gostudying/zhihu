package com.nowcoder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

class Consumer implements Runnable {
	BlockingQueue<Integer> q;

	public Consumer(BlockingQueue<Integer> q) {
		this.q = q;
	}

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println(Thread.currentThread().getName() + ":" + q.take());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Producer implements Runnable {
	BlockingQueue<Integer> q;

	public Producer(BlockingQueue<Integer> q) {
		this.q = q;
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < 100; ++i) {
				Thread.sleep(100);
				q.put(i);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public class BlockingQueueDemo {
	public static void main(String[] args) {
		BlockingQueue<Integer> q = new ArrayBlockingQueue<>(10);
		new Thread(new Producer(q)).start();
		new Thread(new Consumer(q), "Consumer1").start();
		new Thread(new Consumer(q), "Consumer2").start();
	}
}
