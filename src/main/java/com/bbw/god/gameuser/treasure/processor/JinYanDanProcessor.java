package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.leadercard.service.LeaderCardExpService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 经验丹
 * 使用后可获得1000点经验
 */
@Service
public class JinYanDanProcessor extends TreasureUseProcessor{
    @Autowired
    private LeaderCardExpService expService;
    @Override
    public boolean isMatch(int treasureId) {
        return TreasureEnum.FEN_SHEN_JYD.getValue()==treasureId;
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
    public void check(GameUser gu, CPUseTreasure param) {
        TreasureChecker.checkIsEnough(TreasureEnum.FEN_SHEN_JYD.getValue(),1,gu.getId());
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        expService.useJinYanDan(gu.getId(),param.getUseAll()==1,rd);
    }
}
