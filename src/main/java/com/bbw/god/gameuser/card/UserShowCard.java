package com.bbw.god.gameuser.card;
/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月4日 下午11:24:27
 * 类说明 展示卡牌
 */

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserShowCard extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Integer> cardIds = new ArrayList<Integer>();
    private Integer showNum = 3;//默认最多3张

    public static UserShowCard instance(long uid) {
        UserShowCard userShowCard = new UserShowCard();
        userShowCard.setGameUserId(uid);
        userShowCard.setId(ID.INSTANCE.nextId());
        return userShowCard;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_SHOW_CARDS;
    }
}
