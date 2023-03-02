package com.bbw.god.activity.holiday.processor.holidayspcialyeguai;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.cache.TimeLimitCacheUtil;
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

/**
 * 闹鬼南瓜
 *
 * @author fzj
 * @date 2021/10/20 16:41
 */
@Service
public class HolidayNaoGuiNanGuaProcessor extends AbstractSpecialYeGuaiProcessor {
    private static String NAO_GUI_NAN_GUA_GUAI_WEI = "闹鬼南瓜怪味糖豆";

    public HolidayNaoGuiNanGuaProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.NAO_GUI_NAN_GUA);
    }

    @Override
    public void sendActivityExtraAward(long uid, RDFightEndInfo fightEndInfo, RDCommon rd) {
        int activityAwardId = getNaoGuaNanGuaAward(gameUserService.getActiveSid(uid), fightEndInfo);
        if (activityAwardId == 0) {
           return;
        }
        //检查怪味糖豆数量是否到达上限
        activityAwardId = checkGuaiWeiTangNum(uid, activityAwardId);
        TreasureEventPublisher.pubTAddEvent(uid, activityAwardId, 1, WayEnum.WAN_S_ACTIVITY_BOX, rd);
    }


    /**
     * 活动期间，大地图中的部分野怪将变为南瓜幽灵，战胜后开启宝箱时，有几率获得各类糖果（可用于兑换奖励）。失败则会使南瓜幽灵逃遁。
     * 每2小时刷新一次南瓜幽灵位置，可查看大地图中的南瓜头图案进行辨别。
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
        rdFightsInfo.setHead(3180);
        rdFightsInfo.setNickname("南瓜幽灵");
        if (info.getYeGuaiType() == YeGuaiEnum.YG_ELITE.getType()) {
            rdFightsInfo.setNickname("南瓜幽灵（精英）");
        }
        info.updateInfo(rdFightsInfo);
    }

    /**
     * 获得活动闹鬼南瓜对应奖励id
     *
     * @param fightEndInfo
     * @return
     */
    public int getNaoGuaNanGuaAward(int sid, RDFightEndInfo fightEndInfo) {
        if (!isOpened(sid)) {
            return 0;
        }
        YeGuaiEnum yeGtype = fightEndInfo.getYeGtype();
        int seed = getRandom();
        if (YeGuaiEnum.YG_NORMAL.equals(yeGtype) && seed <= 30) {
            return getAwardId();
        }
        if (YeGuaiEnum.YG_ELITE.equals(yeGtype) && seed <= 50) {
            return getAwardId();
        }
        return 0;
    }

    private int getAwardId() {
        int seed = getRandom();
        if (seed <= 2) {
            return TreasureEnum.GUAI_WTD.getValue();
        }
        if (seed <= 41) {
            return TreasureEnum.TANG_SBG.getValue();
        }
        return TreasureEnum.XIONG_XRT.getValue();
    }

    private int getRandom() {
        return PowerRandom.getRandomBySeed(100);
    }

    /**
     * 缓存怪味糖豆数量
     *
     * @param uid
     * @param activityAwardId
     */
    public void cacheGuaiWeiTangNum(long uid, int activityAwardId, int guaiWeiTangNum) {
        if (activityAwardId == TreasureEnum.GUAI_WTD.getValue()) {
            TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, NAO_GUI_NAN_GUA_GUAI_WEI, guaiWeiTangNum + 1, DateUtil.SECOND_ONE_DAY * 10);
        }
    }

    /**
     * 检查怪味糖豆数量如果超过则换成糖霜饼干
     *
     * @param awardId
     * @return
     */
    public int checkGuaiWeiTangNum(long uid, int awardId) {
        Integer guaiWeiTangNum = TimeLimitCacheUtil.getFromCache(uid, NAO_GUI_NAN_GUA_GUAI_WEI, Integer.class);
        guaiWeiTangNum = null == guaiWeiTangNum ? 0 : guaiWeiTangNum;
        if (guaiWeiTangNum >= 100) {
            return TreasureEnum.TANG_SBG.getValue();
        }
        cacheGuaiWeiTangNum(uid, awardId, guaiWeiTangNum);
        return awardId;
    }

}
