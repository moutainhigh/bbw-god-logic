package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgBossMaou;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import org.junit.Test;

public class ServerBossMaouDayConfigServiceTest {

    @Test
    public void getKillerTreasure() {
        for (int i = 0; i < 100; i++) {
            CfgBossMaou bossMaou = Cfg.I.getUniqueConfig(CfgBossMaou.class);
            String treasureName = PowerRandom.getRandomFromList(bossMaou.getKillerAwards());
            CfgTreasureEntity treasureEntity = TreasureTool.getTreasureByName(treasureName);
            System.out.println(treasureEntity);
        }
    }


}