package com.bbw.god.activity.holiday.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.processor.holidaybrocadegift.HolidayBrocadeGiftService;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.businessgang.yingjie.BusinessGangYingJieTaskService;
import com.bbw.god.gameuser.task.businessgang.yingjie.UserBusinessGangYingJieTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 商帮英杰活动
 *
 * @author fzj
 * @date 2022/2/9 13:37
 */
@Service
public class BusinessGangYingJieProcessor extends AbstractActivityProcessor {
    @Autowired
    HolidayBrocadeGiftService holidayBrocadeGiftService;
    @Autowired
    private BusinessGangYingJieTaskService businessGangYingJieTaskService;
    @Autowired
    private GameDataService gameDataService;


    public BusinessGangYingJieProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.BUSINESS_GANG_YINGJIE);
    }


    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.BUSINESS_GANG_YINGJIE.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    /**
     * 任务可领取奖励数量
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        List<UserBusinessGangYingJieTask> tasks = businessGangYingJieTaskService.getCurUseraUserBusinessGangYingJieTasks(gu.getId());
        //每日任务不存在，调用父类方法
        if (ListUtil.isEmpty(tasks)) {
            return super.getAbleAwardedNum(gu, a);
        }
        //返回任务可领取奖励
        return (int) tasks.stream().filter(tmp -> tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()).count();
    }


}
