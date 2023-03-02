package com.bbw.god.game.award.giveback;

import com.bbw.BaseTest;
import com.bbw.common.ID;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GiveBackPoolTest extends BaseTest {

    @Autowired
    private GiveBackPool giveBackPool;
    @Autowired
    private InsRoleInfoService insRoleInfoService;

    @Test
    public void testGiveBackPool() {
        List<Long> uids = insRoleInfoService.getAllUidsByServer(99);
        System.out.println("测试玩家数：" + uids.size());
        List<GiveBackAwards> list = new ArrayList<>();
        List<Award> giveBackAwards = new ArrayList<>();
        giveBackAwards.add(Award.instance(TreasureEnum.DFZ.getValue(), AwardEnum.FB, 2));
        giveBackAwards.add(Award.instance(TreasureEnum.SHSJT.getValue(), AwardEnum.FB, 2));
        for (Long uid : uids) {
            for (int i = 0; i < 50; i++) {
                GiveBackAwards instance = GiveBackAwards.instance(uid, ID.INSTANCE.nextId(), giveBackAwards, "标题", "内容");
                list.add(instance);
            }
        }
        giveBackPool.toGiveBackPool(list);

        giveBackPool.giveBack();
    }
}