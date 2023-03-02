package com.bbw.god.activity.holiday.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 村庄疑云活动
 *
 * @author fzj
 * @date 2021/10/21 17:00
 */
@Service
public class HolidayCunZYiYunProcessor extends AbstractActivityProcessor {


    public HolidayCunZYiYunProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.CUN_Z_YI_YUN);
    }

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 获得任务剩余天数
     * @param uid
     * @return
     */
    public int getActivityRemainDay(long uid){
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CUN_Z_YI_YUN);
        long remainTime = getRemainTime(uid, sid, a) / 1000;
        return (int) (remainTime / (24 * 3600));
    }

    /**
     * 判断是否可以执行任务
     *
     * @param uid
     * @param taskEntity
     * @return
     */
    public boolean isExecutable(long uid, CfgTaskEntity taskEntity, Integer tasksAllNum) {
        int sid = gameUserService.getActiveSid(uid);
        IActivity a = this.activityService.getActivity(sid, ActivityEnum.CUN_Z_YI_YUN);
        if (a == null){
            return false;
        }
        int remainDay = Math.max(getActivityRemainDay(uid) - 1, 0);
        int maxTaskSeq = tasksAllNum - (remainDay * 5);
        return taskEntity.getSeq() <= maxTaskSeq;
    }
}
