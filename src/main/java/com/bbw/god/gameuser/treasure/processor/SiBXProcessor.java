package com.bbw.god.gameuser.treasure.processor;

import org.springframework.stereotype.Service;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;

/**
 * 四不像
 *
 * @author suhq
 * @date 2018年11月29日 上午9:12:21
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SiBXProcessor extends TreasureUseProcessor {

    public SiBXProcessor() {
        this.treasureEnum = TreasureEnum.SBX;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        Long uid = gu.getId();
        int treasureId = this.treasureEnum.getValue();
        Integer addEffect = TreasureTool.getTreasureConfig().getTreasureEffectSBX();
        WayEnum way = WayEnum.TREASURE_USE;
        TreasureEventPublisher.pubTEffectAddEvent(uid, treasureId, addEffect, way, rd);
    }

}
