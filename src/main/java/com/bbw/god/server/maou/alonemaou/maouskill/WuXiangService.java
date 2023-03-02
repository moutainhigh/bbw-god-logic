package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackService;
import com.bbw.god.server.maou.attack.skill.SkillPerformResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author suchaobin
 * @description 无相service
 * @date 2020/8/20 14:32
 **/
@Service
public class WuXiangService implements BaseMaouSkillService {
    @Autowired
    private MaouAttackService maouAttackService;

    private static final List<Integer> EXCLUDE_SKILLS = Arrays.asList(CombatSkillEnum.TJ.getValue(),
            CombatSkillEnum.JKM.getValue(), CombatSkillEnum.MKT.getValue(), CombatSkillEnum.TKS.getValue(),
            CombatSkillEnum.SKF.getValue(), CombatSkillEnum.HKJ.getValue());

    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    @Override
    public int getMyId() {
        return MaouSkillEnum.WU_XIANG.getValue();
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
        List<SkillPerformResult> results = maouAttackService.attack(attackCards);
        return results.stream().filter(tmp -> !EXCLUDE_SKILLS.contains(tmp.getPerformSkill()))
                .mapToInt(SkillPerformResult::gainAckBuf).sum();
    }
}
