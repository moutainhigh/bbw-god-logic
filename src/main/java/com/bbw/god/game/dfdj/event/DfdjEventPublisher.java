package com.bbw.god.game.dfdj.event;

import com.bbw.common.SpringContextUtil;

/**
 * @author suchaobin
 * @description 巅峰对决事件发布器
 * @date 2021/1/6 10:03
 **/
public class DfdjEventPublisher {
    public static void pubDfdjAwardSendEvent(EPDfdjAwardSend ep) {
        SpringContextUtil.publishEvent(new DfdjAwardSendEvent(ep));
    }
}
