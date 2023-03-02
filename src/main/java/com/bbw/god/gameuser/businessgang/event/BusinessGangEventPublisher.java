package com.bbw.god.gameuser.businessgang.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
/**
 * 商帮事件发布器
 *
 * @author fzj
 * @date 2022/1/29 13:46
 */
public class BusinessGangEventPublisher {

    public static void pubAddGangNpcFavorabilityEvent(long uid, int npcId, int addFavorability) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPAddGangNpcFavorability ep = new EPAddGangNpcFavorability(npcId, addFavorability, bep);
        SpringContextUtil.publishEvent(new AddGangNpcFavorabilityEvent(ep));
    }
}
