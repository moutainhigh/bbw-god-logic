package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 圣诞心愿活动
 *
 * @author: huanghb
 * @date: 2022/12/14 17:37
 */
@Service
public class HolidayChristmasWishProcessor extends AbstractActivityProcessor {
    @Autowired
    private HolidayChristmasWishService holidayChristmasWishService;


    public HolidayChristmasWishProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CHRISTMAS_WISH_51);
    }

    /**
     * 是否在ui中展示
     *
     * @param uid
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rdActivityList = (RDActivityList) super.getActivities(uid, activityType);
        //获得心愿礼物信息
        List<UserHolidayChristmasWish> userHolidayChristmasWishs = holidayChristmasWishService.getUserHolidayChristmasWish(uid);
        //封装返回类
        RdChristmasWishs rdChristmasWishs = RdChristmasWishs.instance(userHolidayChristmasWishs);
        rdActivityList.setChristmasWishs(rdChristmasWishs);
        return rdActivityList;
    }

    /**
     * 完成心愿
     *
     * @param uid
     * @param wishGift
     * @param wishId
     * @return
     */
    public RDCommon completeWish(long uid, Integer wishGift, long wishId) {
        //获得所有心愿范围的礼物
        List<Integer> giftWishIds = HolidayChristmasWishTool.getGiftWishIds();
        //是否是心愿范围的礼物
        if (!giftWishIds.contains(wishGift)) {
            throw new ExceptionForClientTip("activity.wishGift.not.exist");
        }
        //心愿任务是否存在
        Optional<UserHolidayChristmasWish> optional = holidayChristmasWishService.getData(uid, wishId);
        if (!optional.isPresent()) {
            throw new ExceptionForClientTip("activity.christmasWish.not.exist");
        }
        //心愿是否已完成
        UserHolidayChristmasWish userHolidayChristmasWish = optional.get();
        if (userHolidayChristmasWish.getStatus() != TaskStatusEnum.DOING.getValue()) {
            throw new ExceptionForClientTip("activity.christmasWish.complete");
        }
        //检查心愿礼物数量
        TreasureChecker.checkIsEnough(wishGift, 1, uid);
        //获得礼物心愿奖励
        Integer giftWish = userHolidayChristmasWish.getGiftWish();
        List<Award> wishAwards = HolidayChristmasWishTool.getWishAwards(wishGift, giftWish);
        //完成心愿
        userHolidayChristmasWish.completeWish();
        //扣除心愿礼物
        RDCommon rd = new RDCommon();
        TreasureEventPublisher.pubTDeductEvent(uid, wishGift, 1, WayEnum.CHRISTMAS_WISH, rd);
        //更新心愿信息
        holidayChristmasWishService.updateData(userHolidayChristmasWish);
        //发送心愿奖励
        awardService.fetchAward(uid, wishAwards, WayEnum.CHRISTMAS_WISH, "", rd);
        return rd;
    }
}
