package com.bbw.god.game.transmigration.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;

/**
 * 轮回事件发布器
 *
 * @author: suhq
 * @date: 2021/9/23 5:55 上午
 */
public class TransmigrationEventPublisher {

    /**
     * 发布轮回成功挑战事件
     *
     * @param uid
     * @param record
     * @param isFirstSuccess
     */
    public static void pubTransmigrationSuccessEvent(long uid, GameTransmigration transmigration, UserTransmigrationRecord record, Boolean isFirstSuccess) {
        BaseEventParam bep = new BaseEventParam(uid);
        EPTransmigrationSuccess ep = new EPTransmigrationSuccess(transmigration, record, isFirstSuccess, bep);
        SpringContextUtil.publishEvent(new TransmigrationSuccessEvent(ep));
    }
}
