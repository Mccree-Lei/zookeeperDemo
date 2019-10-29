package com.lei.zkDemo.curator.barrier;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

public class CuratorBarrier2 {

	/** zookeeper地址 */
	static final String CONNECT_ADDR = "192.168.2.100:2181,192.168.2.101:2181,192.168.2.102:2181";
	/** session超时时间 */
	static final int SESSION_OUTTIME = 5000;//ms 
	
	static DistributedBarrier barrier = null;
	public static void main(String[] args) throws Exception {
		for(int i = 0; i < 5; i++){
			new Thread(new Runnable() {
                public void run() {
                    try {
                        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
                        CuratorFramework cf = CuratorFrameworkFactory.builder()
                                .connectString(CONNECT_ADDR)
                                .retryPolicy(retryPolicy)
                                .build();
                        cf.start();

                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(cf, "/super", 5);
                        Thread.sleep(1000 * (new Random()).nextInt(3));
                        System.out.println(Thread.currentThread().getName() + "已经准备");
                        barrier.enter();
                        System.out.println("同时开始运行...");
                        Thread.sleep(1000 * (new Random()).nextInt(3));
                        System.out.println(Thread.currentThread().getName() + "运行完毕");
                        barrier.leave();
                        System.out.println("同时退出运行...");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
			},"t" + i).start();
		}

		Thread.sleep(5000);
		barrier.removeBarrier();	//释放
		
		
	}
}
