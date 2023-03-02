package com.bbw.god.game.zxz.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.card.equipment.event.EPZxzClearanceScore;

/**
 * 诛仙阵扫荡事件发布器
 *
 * @author: huanghb
 * @date: 2022/9/24 10:34
 */
public class ZxzEventPublisher {


    public static void pubZhiBaoAddEvent(long uid, int difficulty, int clearanceScore) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPZxzClearanceScore ep = EPZxzClearanceScore.instance(bep, difficulty, clearanceScore);
        SpringContextUtil.publishEvent(new ZxzClearanceScoreEvent(ep));
    }
}
