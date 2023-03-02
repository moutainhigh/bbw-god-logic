package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;

/**
 * 卡牌装备事件发布器
 *
 * @author: huanghb
 * @date: 2022/9/24 10:34
 */
public class CardEquipmentEventPublisher {


    public static void pubZhiBaoAddEvent(long uid, WayEnum wayEnum, int zhiBaoId, int property, Integer fullAttackNum, Integer fullDefenseNum) {
        BaseEventParam bep = new BaseEventParam(uid, wayEnum);
        EPCardZhiBaoAdd ep = EPCardZhiBaoAdd.instance(bep, zhiBaoId, property, fullAttackNum, fullDefenseNum);
        SpringContextUtil.publishEvent(new ZhiBaoEvent(ep));
    }

    public static void pubXianJueActiveEvent(long uid, int property, int star) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPXianJueActive ep = EPXianJueActive.instance(bep, property, star);
        SpringContextUtil.publishEvent(new XianJueActiveEvent(ep));
    }

    public static void pubXianJueStudyEvent(long uid, Integer xianJueType) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPXianJueStudy ep = EPXianJueStudy.instance(bep, xianJueType);
        SpringContextUtil.publishEvent(new XianJueStudyEvent(ep));
    }

    public static void pubXianJueUpdataStarEvent(long uid, int xianJueType, int quality) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPXianJueUpdataStar ep = EPXianJueUpdataStar.instance(bep, xianJueType, quality);
        SpringContextUtil.publishEvent(new XianJueUpdataStarEvent(ep));
    }

    public static void pubXianJueComprehendEvent(long uid, int comprehendType) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPXianJueComprehend ep = EPXianJueComprehend.instance(bep, comprehendType);
        SpringContextUtil.publishEvent(new XianJueComprehendEvent(ep));
    }

}
