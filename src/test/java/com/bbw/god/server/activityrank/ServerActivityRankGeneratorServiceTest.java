package com.bbw.god.server.activityrank;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bbw.BaseTest;
import com.bbw.god.activityrank.server.ServerActivityRankGeneratorService;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;

public class ServerActivityRankGeneratorServiceTest extends BaseTest {
	@Autowired
	private ServerActivityRankGeneratorService serverActivityRankGeneratorService;

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
		CfgServerEntity server = Cfg.I.get(99, CfgServerEntity.class);
		serverActivityRankGeneratorService.initServerActivityRankForNewServer(server);
	}

}
