package com.hisaige.core.factory;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
/**
 * 非持久态的异步任务应该优先考虑使用
 * 避免线程频繁创建与销毁，以及多个地方使用线程池时重复创建线程池的过程，
 * 这里使用单例模式创建一个通用的线程处理器
 * @author chenyj
 */
public class ExecutorTaskManager {
	
	
	private static final int CORE_POOL_SIZE = 5;
	
	private static final int MAX_POOL_SIZE = 32;
	
	private static volatile ThreadPoolTaskExecutor threadPoolTaskExecutor = null;
	
	private ExecutorTaskManager() {
	}
	
	/**
	 * 基于Spring的通用线程任务处理器
	 * 双重校验锁 单例
	 * @return 单例 ThreadPoolTaskExecutor
	 */
	private static ThreadPoolTaskExecutor taskExecutor() {
		
		if(null == threadPoolTaskExecutor) {
			synchronized (ExecutorTaskManager.class) {
				if(null == threadPoolTaskExecutor) {
					
					threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
					// 设置核心线程数
					threadPoolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);
					// 设置最大线程数
					threadPoolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);
					// 设置队列容量
					threadPoolTaskExecutor.setQueueCapacity(1000);
					// 设置线程活跃时间（秒）
					threadPoolTaskExecutor.setKeepAliveSeconds(60);
					// 设置默认线程名称
					threadPoolTaskExecutor.setThreadNamePrefix("core-business-task-");
					/**
					 * 核心线程超时会关闭,这里默认设置成false,因为核心线程只有两条
					 * 如果 调用了 setCoreThreadSize 方法处理大任务， 处理完毕后应该重置线程池
					 */
					threadPoolTaskExecutor.setAllowCoreThreadTimeOut(false);
					// 设置拒绝策略--重试添加当前的任务，他会自动重复调用execute()方法 
					threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
					// 等待所有任务结束后再关闭线程池
					//executor.setWaitForTasksToCompleteOnShutdown(true);
					//执行初始化
					threadPoolTaskExecutor.initialize();
				}
			}
		}
		return threadPoolTaskExecutor;
	}
	
	public static ThreadPoolTaskExecutor getTaskExecutor() {
		return taskExecutor();
	}
	
	/**
	 * 返回 java.util.concurrent 包下的线程处理器
	 * @return java.util.concurrent.ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getExecutor() {
		return taskExecutor().getThreadPoolExecutor();
	}
	
	/**
	 * 设置核心线程数
	 * 当使用当前线程池处理大任务时，可将核心线程加大，
	 * 任务处理完毕应该调用<code>resetThreadPool()</code>方法重置线程池
	 * @param size
	 */
	public synchronized static void setCoreThreadSize(int size) {
		if(size > MAX_POOL_SIZE) {
			size = MAX_POOL_SIZE;
		} else if(size < CORE_POOL_SIZE) {
			size = CORE_POOL_SIZE;
		}
		taskExecutor().setCorePoolSize(size);
	}
	
	/**
	 * 设置最大线程数
	 * @param size
	 */
	public synchronized static void setMaxThreadSize(int size) {
		taskExecutor().setMaxPoolSize(size);
	}
	
	/**
	 * 重置线程池配置
	 */
	public synchronized static void resetThreadPool() {
		taskExecutor().setCorePoolSize(CORE_POOL_SIZE);
		taskExecutor().setMaxPoolSize(MAX_POOL_SIZE);
	}
}
