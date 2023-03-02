package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 铜钱娃娃
 *
 * @author fzj
 * @date 2021/12/3 10:09
 */
@Service
public class CopperBabyProcessor extends TreasureUseProcessor {
    @Autowired
    BoxService boxService;
    @Autowired
    GameUserService gameUserService;

    public CopperBabyProcessor() {
        this.treasureEnum = TreasureEnum.COPPER_BABY;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {

    }

    /**
     * 开启宝箱
     *
     * @param uid
     * @param rd
     */
    public void open(long uid, int num, RDCommon rd) {
        //获得宝箱奖励
        Award award = boxService.getAward(uid, TreasureEnum.COPPER_BABY.getValue()).stream().findFirst().orElse(null);
        if (null == award) {
            return;
        }
        int copper = 0;
        for (int i = 0; i < num; i++) {
            int random = PowerRandom.getRandomBetween(5, 8);
            //玩家等级
            Integer level = gameUserService.getGameUser(uid).getLevel();
            copper += random * award.getNum() * level;
        }
        ResEventPublisher.pubCopperAddEvent(uid, copper, WayEnum.COPPER_BABY, rd);
    }
}
