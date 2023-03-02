package com.bbw.god.city.mixd.nightmare.pos;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.mixd.event.MiXDEventPublisher;
import com.bbw.god.city.mixd.nightmare.*;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明：
 * 宝箱:从奖励列表中，根据概率收集1份奖励，收集到的奖励会显示在藏宝背包中。
 * @author lwb
 * date 2021-05-27
 */
@Service
public class BoxSpecialProcessor extends AbstractMiXianPosProcessor {

    @Override
    public boolean match(NightmareMiXianPosEnum miXianPosEnum) {
        return NightmareMiXianPosEnum.BOX_SPECIAL.equals(miXianPosEnum);
    }

    @Override
    public void touchPos(UserNightmareMiXian nightmareMiXian,RDNightmareMxd rd, MiXianLevelData.PosData posData) {
        if (nightmareMiXian.isInTreasureHouse()){
            for (MiXianLevelData.PosData data : nightmareMiXian.getTreasureHouseData().getPosDatas()) {
                if (data.ifXunShiPos()){
                    throw new ExceptionForClientTip("mxd.treasure.house.has.xunshi");
                }
            }
        }
        CfgNightmareMiXian cfg = NightmareMiXianTool.getCfg();
        /**
         * 特殊宝箱可以获得普通宝箱内的所有种类奖励，每种奖励都有25%的概率使该种类额外获得一份奖励。
         */
        List<CfgNightmareMiXian.BoxInfo> normalBoxAwards = cfg.getBoxAwards();
        List<Award> awards=new ArrayList<>();
        for (int i = 0; i < normalBoxAwards.size(); i++) {
            CfgNightmareMiXian.BoxInfo normalBoxAward=normalBoxAwards.get(i);
            awards.addAll(normalBoxAward.getRandomSubAwards());
            if (PowerRandom.hitProbability(25)){
                awards.addAll(normalBoxAward.getRandomSubAwards());
            }
        }
        List<Award> rdAwardList = checkBoxAwards(awards, nightmareMiXian.getGameUserId());
        rd.setGainAwards(rdAwardList);
        nightmareMiXian.addAwardToBag(rdAwardList);
        nightmareMiXian.takeCurrentPosToEmptyType();
        MiXDEventPublisher.pubOpenSpecialBoxEvent(new BaseEventParam(nightmareMiXian.getGameUserId()));
    }
}
