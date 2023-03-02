package com.bbw.god.server.maou.bossmaou;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @author suhq
 * @description: 魔王降临玩家信息
 * @date 2019-12-17 17:48
 **/
@Data
public class UserBossMaouData extends UserSingleObj {
    private HashMap<String, List<Integer>> deckCards = new HashMap<>();//编组卡牌

    public static UserBossMaouData getInstance(long uid) {
        UserBossMaouData obj = new UserBossMaouData();
        obj.setId(ID.INSTANCE.nextId());
        obj.setGameUserId(uid);
        return obj;
    }

    /**
     * 获得参战卡牌
     *
     * @param type
     * @return
     */
    public List<Integer> getAttackingCard(int type) {
        return this.deckCards.get(type + "");
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.BOSS_MAOU_DATA;
    }
}
