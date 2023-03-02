package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author suchaobin
 * @description 聚贤唤神处理器
 * @date 2020/10/27 10:39
 **/
@Service
public class JuXHSProcessor extends AbstractActivityProcessor {

    public JuXHSProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.JU_XIAN_HUAN_SHEN);
    }

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    @Override
    public int getAbleAwardedNum(GameUser gu, IActivity a) {
        return 0;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        return NO_TIME;
    }
}
