package com.bbw.god.activity.holiday.processor.holidayprayerskylantern;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDSkyLanternMessage;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 祈福天灯实现类
 *
 * @author fzj
 * @date 2022/2/8 9:15
 */
@Service
public class HolidayPrayerSkyLanternProcessor extends AbstractActivityProcessor {
    @Autowired
    HolidayPrayerSkyLanternService holidayPrayerSkyLanternService;

    /** 开奖时间 */
    private final static Date DRAW_TIME = DateUtil.fromDateTimeString("2023-02-06 21:00:00");

    public HolidayPrayerSkyLanternProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.PRAYER_SKY_LANTERN);
    }


    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDSkyLanternMessage rd = new RDSkyLanternMessage();
        //获取自己的寄语
        String userMessage = holidayPrayerSkyLanternService.getMessage(uid);
        rd.setUserMessage(userMessage);
        //获取随机四条其他寄语
        List<String> allMessage = holidayPrayerSkyLanternService.getAllMessage();
        if (allMessage.size() > 5) {
            rd.setOtherMessage(PowerRandom.getRandomsFromList(allMessage, 5));
        } else {
            rd.setOtherMessage(allMessage);
        }
        return rd;
    }


    /**
     * 放飞天灯
     *
     * @param uid
     * @param skyLanternNum
     * @param message
     * @return
     */
    public RDCommon putSkyLantern(long uid, int skyLanternNum, String message) {
        //检查天灯数量
        TreasureChecker.checkIsEnough(TreasureEnum.SKY_LANTERN.getValue(), skyLanternNum, uid);
        RDCommon rd = new RDCommon();
        //增加全服放飞天灯次数
        holidayPrayerSkyLanternService.addPutSkyLanternTimes(skyLanternNum);
        //保存寄语
        holidayPrayerSkyLanternService.saveMessage(uid, message);
        //发放随机奖励
        sendRandomAward(uid, skyLanternNum, rd);
        //扣除天灯数量
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SKY_LANTERN.getValue(), skyLanternNum, WayEnum.PUT_SKY_LANTERN, rd);
        return rd;
    }

    /**
     * 发放随机奖励
     *
     * @param uid
     * @param rd
     */
    private void sendRandomAward(long uid, int skyLanternNum, RDCommon rd) {
        List<Award> awards = new ArrayList<>();
        for (int i = 0; i < skyLanternNum; i++) {
            int random = PowerRandom.getRandomBySeed(100);
            int awardId = TreasureEnum.FLOWERS.getValue();
            if (random >= 0 && random <= 15) {
                awardId = TreasureEnum.SKY_LANTERN_LOTTERY.getValue();
                boolean isDraw = DateUtil.now().after(DRAW_TIME);
                if (isDraw) {
                    awardId = TreasureEnum.TREASURE_MAP_FLOP_CARD.getValue();
                }
            }
            if (random > 15) {
                awardId = TreasureEnum.TREASURE_MAP_FLOP_CARD.getValue();
            }
            awards.add(new Award(awardId, AwardEnum.FB, 1));
        }
        Map<Integer, Integer> awardByGroup = awards.stream().filter(a -> a.getItem() == AwardEnum.FB.getValue())
                .collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
        List<Award> awardList = new ArrayList<>(Award.getAwards(awardByGroup, AwardEnum.FB.getValue()));
        awardService.fetchAward(uid, awardList, WayEnum.PUT_SKY_LANTERN, "", rd);
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.PRAYER_SKY_LANTERN.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }
}

