package com.bbw.god.activity.holiday.processor;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 建造祭坛
 *
 * @author fzj
 * @date 2021/12/15 9:58
 */
@Service
public class HolidayBuildingAltarProcessor extends AbstractActivityProcessor {

    @Autowired
    UserTreasureService userTreasureService;
    /** 活动奖励 */
    public static final List<Integer> ACTIVITY_AWARDS = Arrays.asList(TreasureEnum.CUNZ_COIN.getValue(), TreasureEnum.CELEBRATION_POINTS.getValue());
    /** 活动道具 */
    public static final List<Integer> ACTIVITY_TREASURES = Arrays.asList(
            TreasureEnum.GOLDEN_CUP.getValue(),
            TreasureEnum.CANDLE.getValue(),
            TreasureEnum.HINGE.getValue(),
            TreasureEnum.IRON_INGOTS.getValue());

    public HolidayBuildingAltarProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.BUILDING_ALTAR);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    private boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.BUILDING_ALTAR.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return null != a;
    }

    /**
     * 活动捐赠
     *
     * @param uid
     */
    public RDCommon donate(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        //计算最多可捐献的次数
        Integer maxDonateTimes = getMaxDonateTimes(uid);
        //扣除道具
        RDCommon rd = new RDCommon();
        for (Integer treasureId : ACTIVITY_TREASURES) {
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, maxDonateTimes, WayEnum.BUILDING_ALTAR, rd);
        }
        //发放奖励
        sendAward(uid, maxDonateTimes, rd);
        return rd;
    }

    /**
     * 发放活动奖励
     *
     * @param uid
     * @param rd
     */
    private void sendAward(long uid, int maxDonateTimes, RDCommon rd) {
        List<Award> awardList = new ArrayList<>();
        for (int i = 1; i <= maxDonateTimes; i++) {
            for (Integer awardId : ACTIVITY_AWARDS) {
                Award award = Award.instance(awardId, AwardEnum.FB, 1);
                awardList.add(award);
            }
            //额外百分之25的概率发放庆典邀请卡
            if (PowerRandom.hitProbability(25)) {
                Award award = Award.instance(TreasureEnum.CELEBRATION_INVITATION_CARD.getValue(), AwardEnum.FB, 1);
                awardList.add(award);
            }
        }
        Map<Integer, Integer> activityAwards = awardList.stream().collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
        awardService.fetchAward(uid, Award.getAwards(activityAwards, AwardEnum.FB.getValue()), WayEnum.BUILDING_ALTAR, "", rd);
    }

    /**
     * 计算最多可捐献的次数，以活动的四种道具最少的数量为准
     *
     * @param uid
     * @return
     */
    private Integer getMaxDonateTimes(long uid) {
        //获得活动道具拥有数量
        List<Integer> activityTreasures = userTreasureService.getUserTreasures(uid, ACTIVITY_TREASURES)
                .stream().map(UserTreasure::gainTotalNum).filter(t -> t > 0).collect(Collectors.toList());
        //需要同时拥有四种道具
        if (activityTreasures.size() < 4) {
            throw new ExceptionForClientTip("activity.treasue.not.enough");
        }
        return Collections.min(activityTreasures);
    }
}
