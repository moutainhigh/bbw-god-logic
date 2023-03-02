package com.bbw.god.game.dfdj.zone;

import com.bbw.BaseTest;
import com.bbw.god.game.config.server.ServerTool;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DfdjZoneServiceTest extends BaseTest {
    @Autowired
    private DfdjZoneService dfdjZoneService;

    @Test
    public void getZoneByServer() {
        DfdjZone zoneByServer = dfdjZoneService.getZoneByServer(ServerTool.getServer(83));
        System.out.println(zoneByServer);
    }
}