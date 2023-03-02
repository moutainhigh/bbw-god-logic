package com.bbw.god.game.zxz.entity;

import com.bbw.common.ID;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户区域卡组
 * @author: hzf
 * @create: 2022-09-14 20:02
 **/
@Data
public class UserZxzCardGroupInfo extends ZxzAbstractCardGroup implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 区域Id */
    private Integer regionId;

    public static UserZxzCardGroupInfo getInstance(long uId, Integer difficulty, Integer regionId, List<UserZxzCard> cards,long fuCeDataId,ZxzUserLeaderCard zxzUserLeaderCard){
        UserZxzCardGroupInfo cardGroup = new UserZxzCardGroupInfo();
        cardGroup.setId(ID.INSTANCE.nextId());
        cardGroup.setGameUserId(uId);
        cardGroup.setDifficulty(difficulty);
        cardGroup.setRegionId(regionId);
        cardGroup.setCards(cards);
        cardGroup.setZxzUserLeaderCard(zxzUserLeaderCard);
        cardGroup.setFuCeDataId(fuCeDataId);
        return cardGroup;
    }
    public static UserZxzCardGroupInfo getInstance(long uId, Integer difficulty, Integer regionId,long fuCeDataId){
        UserZxzCardGroupInfo cardGroup = new UserZxzCardGroupInfo();
        cardGroup.setId(ID.INSTANCE.nextId());
        cardGroup.setGameUserId(uId);
        cardGroup.setDifficulty(difficulty);
        cardGroup.setRegionId(regionId);
        cardGroup.setFuCeDataId(fuCeDataId);
        return cardGroup;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ZXZ_CARD_GROUP;
    }
}
