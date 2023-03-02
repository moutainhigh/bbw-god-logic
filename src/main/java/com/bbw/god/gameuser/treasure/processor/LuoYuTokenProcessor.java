package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 落羽信物处理器
 * @date 2020/9/22 15:33
 **/
@Service
public class LuoYuTokenProcessor extends TreasureUseProcessor {

    public LuoYuTokenProcessor() {
        this.treasureEnum = TreasureEnum.LUO_YU_XIN_WU;
        this.isAutoBuy = false;
    }

    /**
     * 是否宝箱类
     *
     * @return
     */
    @Override
    public boolean isChestType() {
        return true;
    }

    /**
     * 法宝生效
     *
     * @param gu
     * @param param
     * @param rd
     */
    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        Long uid = gu.getId();
        CardEventPublisher.pubCardAddEvent(uid, 144, WayEnum.ACTIVITY, "打开落羽信物", rd);
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.WNLS3.getValue(), 50, WayEnum.ACTIVITY, rd);
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.SS.getValue(), 100, WayEnum.ACTIVITY, rd);
    }
}
