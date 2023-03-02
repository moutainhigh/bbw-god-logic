package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 加锁service
 * @date 2020/8/20 14:41
 **/
@Service
public class JiaSuoService implements BaseMaouSkillService {
    @Autowired
    private MaouAttackService maouAttackService;

    private static final List<Integer> EXCLUDE_SKILLS = Arrays.asList(CombatSkillEnum.JG.getValue(),
            CombatSkillEnum.ZZ.getValue(),CombatSkillEnum.JIN_ZHAO.getValue()
            ,CombatSkillEnum.JIN_SHEN.getValue(),CombatSkillEnum.CAI_SHEN.getValue());

    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    @Override
    public int getMyId() {
        return MaouSkillEnum.JIA_SUO.getValue();
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
        UserCard targetCard = PowerRandom.getRandomFromList(attackCards);
        List<Integer> targetCardIds = Arrays.asList(targetCard.getBaseId());
        rd.setMaouSkillAction(new RDAloneMaouAttack.RDMaouSkillAction(CombatSkillEnum.JS.getValue(), targetCardIds));
        int maouType = param.getMaou().getType();
        if (canDefend(targetCard)) {
            return maouAttackService.getBeatedBlood(attackCards, maouType);
        }
        return maouAttackService.getBeatedBloodExcludeCards(attackCards, targetCardIds, maouType);
    }

    /**
     * 是否可以防御加锁技能
     *
     * @param targetCard
     * @return
     */
    private boolean canDefend(UserCard targetCard) {
        for (Integer excludeSkill : EXCLUDE_SKILLS) {
            if (targetCard.gainActivedSkills().contains(excludeSkill)) {
                return true;
            }
        }
        return false;
    }
}
