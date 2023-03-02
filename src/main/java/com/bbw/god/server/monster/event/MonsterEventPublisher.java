package com.bbw.god.server.monster.event;

import com.bbw.common.SpringContextUtil;

/**
 * @author suchaobin
 * @description 友怪事件发布推送器
 * @date 2019/12/20 15:49
 */
public class MonsterEventPublisher {

    public static void pubFriendMonsterAddEvent(EPFriendMonsterAdd epFriendMonsterAdd) {
        SpringContextUtil.publishEvent(new FriendMonsterAddEvent(epFriendMonsterAdd));
    }
}
