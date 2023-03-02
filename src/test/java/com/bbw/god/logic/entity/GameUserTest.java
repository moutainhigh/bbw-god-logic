package com.bbw.god.logic.entity;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GameUserTest {

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
		// List<CfgCityEntity> cityEntities = Cfg.INSTANCE.get(CfgCityEntity.class);
		// System.out.println(cityEntities.size());
		for (int i = 0; i < 100; i++) {
			Random random = new Random();
			System.out.println(random.nextInt(5));
		}
	}

}
