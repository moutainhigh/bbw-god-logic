package com.bbw.god.gameuser.card;

import com.bbw.common.ID;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 玩家收藏卡组信息
 * @date 2020/5/8 15:18
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserCollectCardGroup extends UserSingleObj {
    private List<RDShareCardGroup> cardGroups = new LinkedList<>();

    public static UserCollectCardGroup getInstance(long uid) {
        UserCollectCardGroup entity = new UserCollectCardGroup();
        entity.setGameUserId(uid);
        entity.setId(ID.INSTANCE.nextId());
        return entity;
    }

    /**
     * 收藏卡组
     *
     * @param cardGroup 要收藏的卡组
     */
    public void addCardGroup(RDShareCardGroup cardGroup) {
        if (this.cardGroups.size() >= 5) {
            throw new ExceptionForClientTip("card.group.collect.out.of.length");
        }
        if (isAlreadyCollect(cardGroup.getShareId())) {
            throw new ExceptionForClientTip("card.group.already.collect");
        }
        this.cardGroups.add(cardGroup);
    }

    public void delCardGroup(String shareId) {
        if (!isAlreadyCollect(shareId)) {
            throw new ExceptionForClientTip("card.group.not.collect");
        }
        RDShareCardGroup group = this.cardGroups.stream().filter(c ->
                c.getShareId().equals(shareId)).findFirst().orElse(null);
        this.cardGroups.remove(group);
    }

    /**
     * 是否已经收藏过了
     *
     * @param shareId 卡组分享id
     * @return
     */
    private boolean isAlreadyCollect(String shareId) {
        List<String> list = this.cardGroups.stream().map(RDShareCardGroup::getShareId).collect(Collectors.toList());
        return list.contains(shareId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_COLLECT_CARD_GROUP;
    }
}


