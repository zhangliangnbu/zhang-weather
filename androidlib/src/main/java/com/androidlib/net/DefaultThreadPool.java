package com.androidlib.net;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangliang on 16/9/12.
 */
public class DefaultThreadPool {
	// 阻塞队列最大任务数量.线程过多时会进入队列排队
	static final int BLOCKING_QUEUE_SIZE = 20;

	// 线程数
	static final int THREAD_POOL_MAX_SIZE = 10;
	static final int THREAD_POOL_SIZE = 6;

	// task queue
	static ArrayBlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_SIZE);

	// thread pool
	static AbstractExecutorService pool = new ThreadPoolExecutor(
			THREAD_POOL_SIZE,
			THREAD_POOL_MAX_SIZE,
			15L,
			TimeUnit.SECONDS,
			blockingQueue,
			new ThreadPoolExecutor.DiscardPolicy()
	);// if pool size and queue size are full, give up the new task

	private static DefaultThreadPool instance = null;

	// 获取单例
	public static synchronized DefaultThreadPool getInstance() {
		if(instance == null) {
			instance = new DefaultThreadPool();
		}
		return instance;
	}

	// 加入任务并执行
	public void execute(Runnable r) {
		if(r == null) {
			return;
		}

		pool.execute(r);
	}

	// 删除队列中的任务
	public void removeAllTask() {
		blockingQueue.clear();
	}

	// 删除队列中特定任务
	public void removeTaskFromQueue(Object object) {
		blockingQueue.remove(object);
	}

	// 正常关闭线程池
	public void shutdown() {
		if(pool != null) {
			pool.shutdown();
		}
	}

	// 立即关闭线程池
	public void shutdownRightnow() {
		if(pool == null) {
			return;
		}

		pool.shutdown();
		try {
			pool.awaitTermination(1L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
