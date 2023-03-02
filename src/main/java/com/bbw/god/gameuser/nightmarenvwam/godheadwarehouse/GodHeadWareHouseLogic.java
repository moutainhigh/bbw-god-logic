package com.bbw.god.gameuser.nightmarenvwam.godheadwarehouse;

import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.gameuser.nightmarenvwam.NightmareNvWamCfgTool;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author fzj
 * @date 2022/5/10 15:47
 */
@Service
public class GodHeadWareHouseLogic {
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 获得卡牌相关道具
     *
     * @param uid
     * @param cardId
     */
    public RDGodHeadWarehouse getCardRelateTreasure(long uid, int cardId) {
        RDGodHeadWarehouse rd = new RDGodHeadWarehouse();
        List<Integer> treasures = NightmareNvWamCfgTool.getTreasureByCardId(cardId).stream()
                .sorted(Comparator.comparing(Integer::intValue)).collect(Collectors.toList());;;
        List<RDAward> rdAwards = new ArrayList<>();
        for (Integer treasureId : treasures) {
            int treasureNum = userTreasureService.getTreasureNum(uid, treasureId);
            RDAward rdAward = new RDAward(treasureId, AwardEnum.FB, treasureNum);
            rdAwards.add(rdAward);
        }
        rd.setAwards(rdAwards);
        return rd;
    }
}
