package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.HolidayHorseRacingProcessor;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 七彩花
 * @author：lwb
 * @date: 2021/3/9 8:47
 * @version: 1.0
 */
public class ColorfulFlowersProcessor extends TreasureUseProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HolidayHorseRacingProcessor horseRacingProcessor;

    public ColorfulFlowersProcessor() {
        this.treasureEnum = TreasureEnum.COLORFUL_FLOWERS;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        if (!horseRacingProcessor.opened(gameUserService.getActiveSid(gu.getId()))){
            //活动过期
            throw new ExceptionForClientTip("activity.is.timeout");
        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        //使用后将投注

    }
}
