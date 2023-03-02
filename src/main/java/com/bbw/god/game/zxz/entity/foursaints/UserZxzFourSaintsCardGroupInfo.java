package com.bbw.god.game.zxz.entity.foursaints;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.zxz.entity.ZxzAbstractCardGroup;
import com.bbw.god.game.zxz.entity.UserZxzCard;
import com.bbw.god.game.zxz.entity.ZxzFuTu;
import com.bbw.god.game.zxz.entity.ZxzUserLeaderCard;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家四圣挑战卡组
 * @author: hzf
 * @create: 2022-12-28 10:03
 **/
@Data
public class UserZxzFourSaintsCardGroupInfo extends ZxzAbstractCardGroup implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;

    /** 四圣挑战类型 */
    private Integer challengeType;

    public static UserZxzFourSaintsCardGroupInfo getInstance(long uId, Integer challengeType, List<UserZxzCard> cards, long fuCeDataId, ZxzUserLeaderCard zxzUserLeaderCard){
        UserZxzFourSaintsCardGroupInfo cardGroup = new UserZxzFourSaintsCardGroupInfo();
        cardGroup.setId(ID.INSTANCE.nextId());
        cardGroup.setGameUserId(uId);
        cardGroup.setChallengeType(challengeType);
        cardGroup.setCards(cards);
        cardGroup.setZxzUserLeaderCard(zxzUserLeaderCard);
        cardGroup.setFuCeDataId(fuCeDataId);
        return cardGroup;
    }
    public static UserZxzFourSaintsCardGroupInfo getInstance(long uId, Integer challengeType,long fuCeDataId){
        UserZxzFourSaintsCardGroupInfo cardGroup = new UserZxzFourSaintsCardGroupInfo();
        cardGroup.setId(ID.INSTANCE.nextId());
        cardGroup.setGameUserId(uId);
        cardGroup.setChallengeType(challengeType);
        cardGroup.setFuCeDataId(fuCeDataId);
        return cardGroup;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ZXZ_FOUR_SAINTS_CARD_GROUP;
    }
}
