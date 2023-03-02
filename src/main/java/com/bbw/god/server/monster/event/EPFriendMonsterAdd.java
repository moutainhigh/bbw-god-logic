package com.bbw.god.server.monster.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.monster.ServerMonster;
import lombok.Data;

/**
 * @author suchaobin
 * @description 友怪新增事件参数
 * @date 2019/12/20 15:48
 */
@Data
public class EPFriendMonsterAdd extends BaseEventParam {
    private ServerMonster monster;

    public static EPFriendMonsterAdd instance(Long guId, WayEnum way, ServerMonster monster) {
        EPFriendMonsterAdd ev = new EPFriendMonsterAdd();
        ev.setGuId(guId);
        ev.setWay(way);
        ev.setMonster(monster);
        return ev;
    }

    public static EPFriendMonsterAdd instance(Long guId, WayEnum way, RDCommon rd, ServerMonster monster) {
        EPFriendMonsterAdd ev = new EPFriendMonsterAdd();
        ev.setGuId(guId);
        ev.setWay(way);
        ev.setRd(rd);
        ev.setMonster(monster);
        return ev;
    }
}
