package com.bbw.god.gameuser.achievement.resource.card;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.achievement.resource.ResourceAchievementService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就id=14930的service
 * @date 2021/1/22 14:02
 **/
@Service
public class AchievementService14930 extends ResourceAchievementService {
    @Autowired
    private UserCardService userCardService;

    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    @Override
    public int getMyAchievementId() {
        return 14930;
    }

    /**
     * 获取当前成就进度(用于展示给客户端)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        List<UserCard> userCards = userCardService.getUserCards(uid);
        return (int) userCards.stream().filter(tmp ->
                Arrays.asList(201, 456, 457).contains(CardTool.getNormalCardId(tmp.getBaseId())) && tmp.getLevel() >= 10).count();
    }

    /**
     * 获取当前资源类型
     *
     * @return 当前资源类型
     */
    @Override
    public AwardEnum getMyAwardEnum() {
        return AwardEnum.KP;
    }
}
