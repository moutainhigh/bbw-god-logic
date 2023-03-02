package com.bbw.god.gameuser.card.juling;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 玩家聚灵界信息
 * @date 2020/2/21 14:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserJuLJInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 上次聚仙棋获得的随机卡牌id */
    private Integer lastJxqCard;

    public UserJuLJInfo(Long uid) {
        this.id = ID.INSTANCE.nextId();
        this.gameUserId = uid;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.JLJ;
    }
}
