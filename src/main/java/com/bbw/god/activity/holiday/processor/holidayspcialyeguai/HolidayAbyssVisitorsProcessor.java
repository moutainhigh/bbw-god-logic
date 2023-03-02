package com.bbw.god.activity.holiday.processor.holidayspcialyeguai;


import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;

import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


/**
 * @author fzj
 * @description: 深渊来客
 * @date 2021-12-14 16:20
 **/
@Service
public class HolidayAbyssVisitorsProcessor extends AbstractSpecialYeGuaiProcessor {
    /** 活动道具 */
    public static final List<Integer> ACTIVITY_TREASURES = Arrays.asList(
            TreasureEnum.GOLDEN_CUP.getValue(),
            TreasureEnum.CANDLE.getValue(),
            TreasureEnum.HINGE.getValue(),
            TreasureEnum.IRON_INGOTS.getValue());

    private static final String SPECIAL_YE = "深渊恶魔";
    private static final String SPECIAL_JYE = "深渊恶魔（精英）";

    public HolidayAbyssVisitorsProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.ABYSS_VISITORS);
    }


    @Override
    public void changeFightInfo(long uid, RDArriveYeG info, RDFightsInfo rdFightsInfo) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        if (!hasYouHun(uid, info.getArriveCityId())) {
            return;
        }
        rdFightsInfo.setHead(TreasureEnum.ABYSS_DEMON_HEAD.getValue());
        rdFightsInfo.setNickname(SPECIAL_YE);
        if (info.getYeGuaiType() == YeGuaiEnum.YG_ELITE.getType()) {
            rdFightsInfo.setNickname(SPECIAL_JYE);
        }
        info.updateInfo(rdFightsInfo);
    }

    /**
     * 发放活动奖励
     *
     * @param uid
     * @param rd
     */
    @Override
    public void sendActivityExtraAward(long uid, RDFightEndInfo fightEndInfo, RDCommon rd) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        boolean specialYeGuai = fightEndInfo.getNickname().equals(SPECIAL_YE) || fightEndInfo.getNickname().equals(SPECIAL_JYE);
        if (!specialYeGuai) {
            return;
        }
        //发放随机奖励
        Integer randomAward = PowerRandom.getRandomFromList(ACTIVITY_TREASURES);
        TreasureEventPublisher.pubTAddEvent(uid, randomAward, 1, WayEnum.ABYSS_VISITORS, rd);
    }

}
