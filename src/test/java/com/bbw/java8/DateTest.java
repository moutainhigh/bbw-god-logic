package com.bbw.java8;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.bbw.common.DateUtil;

public class DateTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Date beginDate = DateUtil.fromDateTimeString("2018-07-25 11:00:00");
		Date endDate = DateUtil.fromDateTimeString("2019-03-29 11:00:00");
		// Date monthBegin = DateUtil.getMonthBegin(baseDate, 0);
		// Date monthEnd = DateUtil.getMonthEnd(baseDate, 0);
		// // 将时间转换为起始和结束时间
		// Date dateBegin = DateUtil.getDateBegin(monthBegin);
		// Date dateEnd = DateUtil.getDateEnd(monthEnd);
		// System.out.println(monthBegin.toString());
		// System.out.println(monthEnd.toString());
		// System.out.println(dateBegin.toString());
		// System.out.println(dateEnd.toString());
		// System.out.println(DateUtil.getMonthSince(DateUtil.fromDateTimeString("2018-01-01
		// 10:00:00")));
		System.out.println(DateUtil.addDays(DateUtil.now(), 6));
		System.out.println(DateUtil.getDaysBetween(beginDate, endDate) / 7);
		System.out.println(DateUtil.getDaysBetween(DateUtil.now(), DateUtil.fromDateTimeString("2019-03-28 23:59:59")));
	}

}
