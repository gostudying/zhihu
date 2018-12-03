package com.nowcoder;

class Ticket implements Runnable {
	int ticket = 100;

	public synchronized void run() {
		while (ticket > 0) {
			ticket--;
			System.out.println(Thread.currentThread().getName()+":" + ticket);
		}
	}
}

public class LockDemo {
	public static void main(String[] args) {
		Ticket ticket = new Ticket();
		Thread t1 = new Thread(ticket);
		Thread t2 = new Thread(ticket);
		
		t1.start();
		t2.start();
	}
}
