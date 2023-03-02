package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.leadercard.service.LeaderCardExpService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 超级经验丹
 * 一次性升到5级：否则给10W经验
 */
@Service
public class JinYanDanPlusProcessor extends TreasureUseProcessor{
    @Autowired
    private LeaderCardExpService expService;
    @Override
    public boolean isMatch(int treasureId) {
        return TreasureEnum.CHAO_JI_JYD.getValue()==treasureId;
    }

    @Override
    public boolean isSelfToDeductTreasure(long uid) {
        return true;
    }

    @Override
    public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
        return 0;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        expService.useJinYanDanPlus(gu.getId(),rd);
    }
}
