package com.bbw.god.activity.processor.cardboost;

import com.bbw.common.ID;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 助力卡牌
 *
 * @author: suhq
 * @date: 2021/8/3 3:58 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBoostCards extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer[] cardIds = new Integer[]{0, 0, 0};
    private boolean isUpdated = false;

    public static UserBoostCards instance(long uid, Integer[] cardIds) {
        UserBoostCards instance = new UserBoostCards();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setCardIds(cardIds);
        return instance;
    }

    public boolean ifBoostCard(int cardId) {
        return gainIndex(cardId) >= 0;
    }

    public int gainIndex(int cardId) {
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i] == cardId || cardIds[i] == cardId % CardTool.DeifyBase) {
                return i;
            }
        }
        return -1;
    }

    public void setCard(int index, int cardId) {
        cardIds[index] = cardId;
    }

    public void removeCard(int cardId) {
        for (int i = 0; i < cardIds.length; i++) {
            if (cardIds[i] == cardId || cardIds[i] == cardId / CardTool.DeifyBase) {
                cardIds[i] = 0;
            }
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.BOOST_CARDS;
    }
}
