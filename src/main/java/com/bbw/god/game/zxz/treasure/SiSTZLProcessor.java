package com.bbw.god.game.zxz.treasure;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.service.ZxzRefreshService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.processor.TreasureUseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 四圣挑战令
 * @author: hzf
 * @create: 2023-01-02 12:04
 **/
@Service
public class SiSTZLProcessor extends TreasureUseProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;
    @Autowired
    private ZxzRefreshService zxzRefreshService;


    /** 免费次数 */
    int freeRefreshFrequency = 0;

    public SiSTZLProcessor() {
        this.treasureEnum = TreasureEnum.ZXZ_SSTZL;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        UserZxzFourSaintsInfo userZxzFourSaints = zxzFourSaintsService.getUserZxzFourSaints(gu.getId(), param.getChallengeType());
        if (userZxzFourSaints.getWeeklyFirstClearance()) {
            throw new ExceptionForClientTip("zxz.for.saints.get.exploratoryPoint");
        }
        freeRefreshFrequency = userZxzFourSaints.getFreeRefreshFrequency();
    }

    @Override
    public int getNeedNum(GameUser gu, int useTimes, WayEnum way) {
        return needConsumeNum(freeRefreshFrequency);
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        //刷新规则
        zxzRefreshService.manualRefreshFourSaintsChallenge(gu.getId(), param.getChallengeType());
    }

    /**
     * 需要消耗的数量
     * @return
     */
    private int needConsumeNum(int freeRefreshFrequency){
        if (freeRefreshFrequency > 0) {
            return 0;
        }
        return 1;
    }
}
