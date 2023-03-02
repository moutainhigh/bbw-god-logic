package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 灵动service
 * @date 2020/8/20 14:47
 **/
@Service
public class LingDongService implements BaseMaouSkillService {
    @Autowired
    private MaouAttackService maouAttackService;

    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    @Override
    public int getMyId() {
        return MaouSkillEnum.LING_DONG.getValue();
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
        List<Integer> targetCardIds = new ArrayList<>();
        List<Integer> noShenJianCardIds = new ArrayList<>();
        attackCards.forEach(attackCard -> {
            boolean isLingDong = PowerRandom.getRandomBySeed(100) <= 70;
            if (isLingDong) {
                targetCardIds.add(attackCard.getBaseId());
                if (!attackCard.gainActivedSkills().contains(CombatSkillEnum.SHENJ.getValue())) {
                    noShenJianCardIds.add(attackCard.getBaseId());
                }
            }
        });
        int maouType = param.getMaou().getType();
        rd.setMaouSkillAction(new RDAloneMaouAttack.RDMaouSkillAction(CombatSkillEnum.LINGD.getValue(), targetCardIds));
        return this.maouAttackService.getBeatedBloodExcludeCards(attackCards, noShenJianCardIds, maouType);
    }
}
