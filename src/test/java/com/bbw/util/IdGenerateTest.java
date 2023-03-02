package com.bbw.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.common.IdGenerate;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月26日 上午10:18:00
 */
public class IdGenerateTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getServerId() {
		// 6位日期(yyMMdd)+4位原始区服ID+5位区服玩家计数器
		Long uid = 190416212590070L;
		Long sid = uid / 100000 % 10000;
		System.out.println(sid);
	}

	@Test
	public void test() {
		System.out.println(IdGenerate.class.getSimpleName());
		IdGenerate idGenerate = new IdGenerate(0, 0);
		//		int count = 100000;//线程数=count*count
		//		final long[][] times = new long[count][100];
		//
		//		Thread[] threads = new Thread[count];
		//		for (int i = 0; i < threads.length; i++) {
		//			final int ip = i;
		//			threads[i] = new Thread() {
		//				@Override
		//				public void run() {
		//					for (int j = 0; j < 100; j++) {
		//						long t1 = System.nanoTime();//该函数是返回纳秒的。1毫秒=1纳秒*1000000
		//
		//						idGenerate.nextId();//测试
		//
		//						long t = System.nanoTime() - t1;
		//
		//						times[ip][j] = t;//求平均
		//					}
		//				}
		//
		//			};
		//		}
		//
		//		long lastMilis = System.currentTimeMillis();
		//		//逐个启动线程
		//		for (int i = 0; i < threads.length; i++) {
		//			threads[i].start();
		//		}
		//
		//		for (int i = 0; i < threads.length; i++) {
		//			try {
		//				threads[i].join();
		//			} catch (InterruptedException e) {
		//				e.printStackTrace();
		//			}
		//		}
		//		/**
		//		 * 1、QPS：系统每秒处理的请求数（query per second）
		//		   2、RT：系统的响应时间，一个请求的响应时间，也可以是一段时间的平均值
		//		   3、最佳线程数量：刚好消耗完服务器瓶颈资源的临界线程数
		//		    对于单线程：QPS=1000/RT
		//		    对于多线程：QPS=1000*线程数量/RT
		//		 */
		//		long time = System.currentTimeMillis() - lastMilis;
		//		System.out.println("QPS: " + (1000 * count / time));
		//
		//		long sum = 0;
		//		long max = 0;
		//		for (int i = 0; i < times.length; i++) {
		//			for (int j = 0; j < times[i].length; j++) {
		//				sum += times[i][j];
		//
		//				if (times[i][j] > max)
		//					max = times[i][j];
		//			}
		//		}
		//		System.out.println("Sum(ms)" + time);
		//		System.out.println("AVG(ms): " + sum / 1000000 / (count * 100));
		//		System.out.println("MAX(ms): " + max / 1000000);
		System.out.println("idGenerate.nextId(): " + idGenerate.nextId());
		System.out.println("idGenerate.nextId(): " + idGenerate.nextCode("11"));
		System.out.println(ID.INSTANCE.nextId());
		System.out.println(ID.INSTANCE.nextId());
		Long id = ID.INSTANCE.nextId();
		System.out.println(String.valueOf(id).length());
		System.out.println(DateUtil.fromDateInt(20190308).getTime());
	}

}
