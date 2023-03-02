package com.bbw.god.server.activity;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbw.BaseTest;
import com.bbw.god.activity.server.ServerActivityGeneratorService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;

public class ServerActivityGeneratorServiceTest extends BaseTest {
	@Autowired
	private ServerActivityGeneratorService serverActivityGeneratorService;

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
		// fail("Not yet implemented");
		CfgServerEntity server = Cfg.I.get(99, CfgServerEntity.class);
		serverActivityGeneratorService.initServerActivityForNewServer(server);
	}

}
