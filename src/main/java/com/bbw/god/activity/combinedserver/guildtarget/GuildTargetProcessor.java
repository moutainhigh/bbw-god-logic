package com.bbw.god.activity.combinedserver.guildtarget;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 合服活动行会目标
 *
 * @author fzj
 * @date 2022/2/14 15:35
 */
@Service
public class GuildTargetProcessor extends AbstractActivityProcessor {

    public GuildTargetProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GUILD_TARGET);
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
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.GUILD_TARGET.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }
}
