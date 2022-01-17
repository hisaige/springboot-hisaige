package com.hisaige.core.concurrent;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 线程执行器管理
 * @author chenyj
 */
public class ThreadPoolExecutorManager {

	private static final Logger logger = LoggerFactory.getLogger(ThreadPoolExecutorManager.class);
    /**
     * 线程执行器
     */
    private static volatile ExecutorService cachedPoolService = null;

    /**
     * 带回调处理的线程执行器
     * 使用方式
     * 1.添加任务
     *   future = listeningExecutorService.submit(new Callable<String>() {
     *             public String call() throws Exception {
     *                 todo...
     *                 return "...";
     *             }
     *         });
     *2.任务回调
     *    Futures.addCallback(future, new FutureCallback<String>() {
     *             public void onSuccess(String result) {
     *                 todo...
     *             }
     *             public void onFailure(Throwable t) {
     *                 t.printStackTrace();
     *             }
     *         }, executor);
     */
    private static volatile ListeningExecutorService listeningExecutorService;
    
    
    private static volatile ThreadPoolTaskExecutor threadPoolTaskExecutor = null;


    /**
     * 创建一个可缓存的通用线程池
     * 如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
     * 注意 使用本线程池处理并发任务时，消费速度必须比生产速度快，或者任务数量限制在有限值内
     * 否则会无限创建线程导致服务崩溃，
     * 如果任务数量比较多，或者任务数不确定，请使用阿里巴巴开发手册推荐的方法获取线程处理器处理大任务
     */
    public static ExecutorService cachedPoolService() {
    	
        if(null == cachedPoolService) {
            synchronized (ThreadPoolExecutorManager.class) {
                if(null == cachedPoolService) {
                    cachedPoolService = Executors.newCachedThreadPool(new DefaultThreadFactory("cachedPool"));
                }
            }
        }
        return cachedPoolService;
    }
    
    /**
     * 基于Spring的通用线程任务处理器
     * 
     * 结合cachedPoolService使用，采用 CallerRunsPolicy 拒绝策略
     * 	     当ThreadPoolTaskExecutor中的任务塞满队列时，cachedPoolService线程池中的一条线程加入处理
	 * @return 单例 ThreadPoolTaskExecutor
	 */
	public static ThreadPoolTaskExecutor taskExecutor() {

		if (null == threadPoolTaskExecutor) {
			synchronized (ThreadPoolExecutorManager.class) {
				if (null == threadPoolTaskExecutor) {

					threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
					// 设置核心线程数
					threadPoolTaskExecutor.setCorePoolSize(50);
					// 设置最大线程数
					threadPoolTaskExecutor.setMaxPoolSize(50);
					// 设置队列容量,默认为10000，有任务直接移交到线程池中处理，创建线程任务时注意命名
					threadPoolTaskExecutor.setQueueCapacity(10000);
					// 设置线程活跃时间（秒）
					threadPoolTaskExecutor.setKeepAliveSeconds(120);
					//核心线程也因超过活跃时间而超时
					threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
					// 设置默认线程名称
					threadPoolTaskExecutor.setThreadNamePrefix("Core-business-task-");
					/**
					 * 核心线程超时会关闭,这里默认设置成false 如果 调用了 setCoreThreadSize 方法处理大任务， 处理完毕后应该重置线程池
					 */
					threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
					// 设置拒绝策略--重试添加当前的任务，如果队列已满，则由当前线程执行任务
					threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
					// 等待所有任务结束后再关闭线程池
					// executor.setWaitForTasksToCompleteOnShutdown(true);
					// 执行初始化
					threadPoolTaskExecutor.initialize();
				}
			}
		}
		return threadPoolTaskExecutor;
	}

	/**
	 * 带回调的异步执行器
	 * 目前用于http请求异步方法用到
	 * 拒绝策列采用 AbortPolicy ，如果任务队列已满，则抛出异常并丢弃任务
	 * @return ListeningExecutorService
	 */
    public static ListeningExecutorService listeningExecutorService() {
        if(null == listeningExecutorService) {
            synchronized (ThreadPoolExecutorManager.class) {
                if(null == listeningExecutorService) {
					ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue(1000000), new DefaultThreadFactory("HttpThread-pool"), new ThreadPoolExecutor.AbortPolicy());
					listeningExecutorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
                }
            }
        }
        return listeningExecutorService;
    }
    
    /**
	 * The default thread factory
	 */
	public static class DefaultThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String namePrefix;

		DefaultThreadFactory(String prefix) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null) ? s.getThreadGroup() :
					Thread.currentThread().getThreadGroup();
			//目前仅用于cachedPoolService
			namePrefix = prefix + "-thread-";
		}

		public Thread newThread(Runnable r) {
			Thread t = new Thread(group, r,
					namePrefix + threadNumber.getAndIncrement(),
					0);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
}
