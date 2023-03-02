package com.bbw.god.gameuser.card;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户编组
 *
 * @author suhq
 * @date 2019-09-06 09:13:51
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCardGroup extends UserData implements Serializable {
    public static final UserDataType DATA_TYPE = UserDataType.CARD_GROUP;
    public static final String DEFAULT_NAME_PREFIX = "卡组";
    private static final long serialVersionUID = 1L;

    private String name;// 卡组名称
    private List<Integer> cards;// 编组卡牌
    private Integer fuCe = 0;//符册标识
    private Integer groupWay = CardGroupWay.Normal_Fight.getValue();
    private Boolean isUsing = false;
    private Integer groupNumber = null;//卡组编号

    public static UserCardGroup instance(long guId, int deck) {
        return instance(guId, deck, new ArrayList<>());
    }

    public static UserCardGroup instance(long guId, int deck, List<Integer> cards) {
        UserCardGroup uCardGroup = new UserCardGroup();
        uCardGroup.setId(ID.INSTANCE.nextId());
        uCardGroup.setGameUserId(guId);
        uCardGroup.setName(DEFAULT_NAME_PREFIX + deck);
        uCardGroup.setGroupNumber(deck);
        uCardGroup.setCards(cards);
        return uCardGroup;
    }

    public static UserCardGroup instance(long guId, int deck, CardGroupWay cardGroupWay, List<Integer> cards) {
        UserCardGroup uCardGroup = new UserCardGroup();
        uCardGroup.setId(ID.INSTANCE.nextId());
        uCardGroup.setGameUserId(guId);
        uCardGroup.setName(cardGroupWay.getName());
        uCardGroup.setGroupNumber(deck);
        uCardGroup.setGroupWay(cardGroupWay.getValue());
        uCardGroup.setCards(cards);
        return uCardGroup;
    }

    public Integer getGroupNumber() {
        if (groupNumber == null) {
            return Integer.parseInt(name.substring(name.length() - 1));
        }
        return groupNumber;
    }

    public void setNewName(String name) {
        if (groupNumber == null) {
            groupNumber = Integer.parseInt(this.name.substring(this.name.length() - 1));
        }
        this.name = name;
    }

    @Override
    public UserDataType gainResType() {
        return DATA_TYPE;
    }

}
