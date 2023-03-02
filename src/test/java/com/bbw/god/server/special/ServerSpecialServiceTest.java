package com.bbw.god.server.special;

import com.bbw.BaseTest;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

public class ServerSpecialServiceTest extends BaseTest {

	@Autowired
	private ServerSpecialService serverSpecialService;

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
		//		serverSpecialService.initSpecialPriceForNewServer(server);
	}

}
