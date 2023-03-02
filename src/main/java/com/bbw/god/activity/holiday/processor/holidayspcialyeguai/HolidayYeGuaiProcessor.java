package com.bbw.god.activity.holiday.processor.holidayspcialyeguai;


import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author lwb
 * @description: 贼寇来袭
 * @date 2020-08-27 14:20
 **/
@Service
public class HolidayYeGuaiProcessor extends AbstractSpecialYeGuaiProcessor {

    public HolidayYeGuaiProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_YEGUAI);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    @Override
    public void sendActivityExtraAward(long uid, RDFightEndInfo fightEndInfo, RDCommon rd) {
        //5%概率额外刷出挖宝铲子
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        boolean isSpecialYeGuai = fightEndInfo.ifNickname("盗宝贼寇") || fightEndInfo.ifNickname("盗宝贼寇（精英）");
        if ((!isSpecialYeGuai || !PowerRandom.hitProbability(5))) {
            return;
        }
        IActivity activity = activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.HOLIDAY_DIG_FOR_TREASURE);
        if (activity == null || !activity.ifTimeValid()) {
            return;
        }
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.CHANG_ZI.getValue(), 1, getWay(), rd);
    }



    /**
     * 1.活动期间，将在大地图所有野怪图标上增加游魂标识，踩在拥有游魂标识的野怪格上时，弹出的标题为“精英游魂来袭”或“游魂来袭”。
     * 生成的野怪将会变更为游魂的头像（老版鬼道士头像），名称变为“游魂”或“游魂（精英）”。
     * 2.击败游魂时，将根据变更为游魂的野怪（普通或者精英，概率跟常规野怪相同）获得对应的驱魔点（用于参与除魔卫道榜，详见下文）。
     *
     * @param uid
     * @param info
     */
    @Override
    public void changeFightInfo(long uid, RDArriveYeG info, RDFightsInfo rdFightsInfo) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return;
        }
        if (!hasYouHun(uid, info.getArriveCityId())) {
            return;
        }
        rdFightsInfo.setHead(3140);
        rdFightsInfo.setNickname("盗宝贼寇");
        if (info.getYeGuaiType() == YeGuaiEnum.YG_ELITE.getType()) {
            rdFightsInfo.setNickname("盗宝贼寇（精英）");
        }
        info.updateInfo(rdFightsInfo);
    }
}
