package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 筑基丹处理器
 * @date 2020/11/25 20:19
 **/
@Service
public class ZhuJDProcessor extends TreasureUseProcessor{

    public ZhuJDProcessor() {
        this.isAutoBuy = false;
        this.treasureEnum = TreasureEnum.ZHU_JI_DAN;
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
        useZhuJiDan(gu, rd);
    }

    public void useZhuJiDan(GameUser gu, RDCommon rd) {
        GameUser.Status status = gu.getStatus();
        Integer level = gu.getLevel();
        // 已使用筑基丹或者使用筑基丹无效
        if (level >= 60 || status.getIsUseZhuJiDan()) {
            ResEventPublisher.pubGoldAddEvent(gu.getId(), 100, WayEnum.EXPIRE, rd);
        }
        status.setIsUseZhuJiDan(true);
        gu.updateStatus();
    }
}
