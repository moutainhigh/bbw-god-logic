package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 登录有礼
 *
 * @author: huanghb
 * @date: 2021/12/29 16:52
 */
@Service
public class LoginAwardProcessor extends AbstractActivityProcessor {

    public LoginAwardProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.LOGIN_AWARD);
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
}
