package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;

import java.util.List;

/**
 * @author suchaobin
 * @description 基础魔王技能service
 * @date 2020/8/20 13:55
 **/
public interface BaseMaouSkillService {
    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    int getMyId();

    /**
     * 获取魔王损失的血量
     *
     * @param uid         玩家id
     * @param param       独占魔王参数
     * @param attackCards 攻击的卡牌
     * @param rd          返回给客户端数据
     * @return 魔王损失血量
     */
    int getBeatedBlood(long uid, AloneMaouParam param, List<UserCard> attackCards, RDAloneMaouAttack rd);
}
