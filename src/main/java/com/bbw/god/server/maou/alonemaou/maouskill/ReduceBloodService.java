package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 减伤10%service
 * @date 2020/8/20 14:21
 **/
@Service
public class ReduceBloodService implements BaseMaouSkillService {
    @Autowired
    private MaouAttackService maouAttackService;

    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    @Override
    public int getMyId() {
        return MaouSkillEnum.REDUCE_BLOOD.getValue();
    }

    /**
     * 获取魔王损失的血量
     *
     * @param uid         玩家id
     * @param param       独占魔王参数
     * @param attackCards 攻击的卡牌
     * @param rd          返回给客户端数据
     * @return 魔王损失血量
     */
    @Override
    public int getBeatedBlood(long uid, AloneMaouParam param, List<UserCard> attackCards, RDAloneMaouAttack rd) {
        Integer maouType = param.getMaou().getType();
        return (int) (this.maouAttackService.getBeatedBlood(attackCards, maouType) * 0.9);
    }
}
