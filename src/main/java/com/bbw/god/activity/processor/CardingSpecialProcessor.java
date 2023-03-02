package com.bbw.god.activity.processor;

import com.bbw.common.DateUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.gameuser.GameUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

/**
 * @author suhq
 * @description: 抽卡特惠
 * @date 2019-11-07 09:20
 **/
@Service
public class CardingSpecialProcessor extends AbstractActivityProcessor {

    public CardingSpecialProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.DRAW_CARD_TH);
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        GameUser gu = gameUserService.getGameUser(uid);
        Date regTime = gu.getRoleInfo().getRegTime();
        Date endDate = DateUtil.addSeconds(regTime, 7 * 24 * 60 * 60 - 1);
        return endDate.getTime() - DateUtil.now().getTime();
    }
}
