package com.bbw.god.server.maou.alonemaou.maouskill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouParam;
import com.bbw.god.server.maou.alonemaou.rd.RDAloneMaouAttack;
import com.bbw.god.server.maou.attack.MaouAttackService;
import com.bbw.god.server.maou.attack.skill.SkillPerformResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 联攻无效service
 * @date 2020/8/20 14:15
 **/
@Service
public class IgnoreLianGongService implements BaseMaouSkillService {
    @Autowired
    private MaouAttackService maouAttackService;

    /**
     * 获取当前service对应id
     *
     * @return 当前service对应id
     */
    @Override
    public int getMyId() {
        return MaouSkillEnum.IGNORE_LIAN_GONG.getValue();
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
        int beatedBlood = results.stream()
                .filter(tmp -> tmp.getPerformSkill() != CombatSkillEnum.LG.getValue())
                .mapToInt(SkillPerformResult::gainAckBuf).sum();
        int maouType = param.getMaou().getType();
        int ackBufAsXiangKe = maouAttackService.getAckBufAsXiangKe(results, maouType);
        return beatedBlood + ackBufAsXiangKe;
    }
}
