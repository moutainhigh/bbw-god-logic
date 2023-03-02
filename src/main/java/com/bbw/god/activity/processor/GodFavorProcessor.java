package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

/**
 * @author suchaobin
 * @description 仙人垂青处理器
 * @date 2020/8/11 16:50
 **/
@Service
public class GodFavorProcessor extends AbstractActivityProcessor {

    public GodFavorProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.GOD_FAVOR);
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
        GameUser gu = gameUserService.getGameUser(uid);
        Date regTime = gu.getRoleInfo().getRegTime();
        Date endDate = DateUtil.addSeconds(regTime, 7 * 24 * 60 * 60 - 1);
        return endDate.getTime() - DateUtil.now().getTime();
    }
}
