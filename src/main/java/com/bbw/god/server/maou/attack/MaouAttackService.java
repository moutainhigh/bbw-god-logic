package com.bbw.god.server.maou.attack;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.CfgAloneMaou;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.maou.alonemaou.AloneMaouTool;
import com.bbw.god.server.maou.attack.skill.ISkillService;
import com.bbw.god.server.maou.attack.skill.SkillPerformParam;
import com.bbw.god.server.maou.attack.skill.SkillPerformResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 魔王攻击服务
 * @date 2019-12-31 17:19
 **/
@Service
public class MaouAttackService {
    @Autowired
    private List<ISkillService> skillServices;

    /**
     * 获取特定卡牌对某一属性的魔王的攻击血量
     *
     * @param cards
     * @param maouType
     * @return
     */
    public int getBeatedBlood(List<UserCard> cards, int maouType) {
        List<SkillPerformResult> results = attack(cards);
        int beatedBlood = results.stream().mapToInt(SkillPerformResult::gainAckBuf).sum();
        int ackBufAsXiangKe = getAckBufAsXiangKe(results, maouType);
        return beatedBlood + ackBufAsXiangKe;
    }

    /**
     * 获取特定卡牌对某一属性的魔王的攻击血量,并忽略部分卡牌的效果
     *
     * @param cards
     * @param excludeCards
     * @param maouType
     * @return
     */
    public int getBeatedBloodExcludeCards(List<UserCard> cards, List<Integer> excludeCards, int maouType) {
        List<SkillPerformResult> results = attack(cards);
        int beatedBlood = results.stream()
                .filter(tmp -> !excludeCards.contains(tmp.getEffectedCard().getId()))
                .mapToInt(SkillPerformResult::gainAckBuf).sum();
        int ackBufAsXiangKe = getAckBufAsXiangKe(results, maouType);
        return beatedBlood + ackBufAsXiangKe;
    }

    /**
     * 获取相克加成
     *
     * @param results
     * @param maouType
     * @return
     */
    public int getAckBufAsXiangKe(List<SkillPerformResult> results, int maouType) {
        return results.stream()
                .filter(tmp -> tmp.getPerformSkill() == CombatSkillEnum.NORMAL_ATTACK.getValue())
                .mapToInt(tmp -> {
                    int cardType = tmp.getPerformCard().getType().getValue();
                    if (isXiangKe(cardType, maouType)) {
                        Double phaseAckBuf = tmp.getAckBuf() * 0.5;
                        return phaseAckBuf.intValue();
                    }
                    return 0;
                }).sum();
    }

    /**
     * 获取属性反转的攻击加成
     *
     * @param results
     * @param maouType
     * @return
     */
    public int getAckBufAsXiangKeReverse(List<SkillPerformResult> results, int maouType) {
        return results.stream()
                .filter(tmp -> tmp.getPerformSkill() == CombatSkillEnum.NORMAL_ATTACK.getValue())
                .mapToInt(tmp -> {
                    int cardType = tmp.getPerformCard().getType().getValue();
                    if (isXiangKeAsReverse(cardType, maouType)) {
                        Double phaseAckBuf = tmp.getAckBuf() * 0.5;
                        return phaseAckBuf.intValue();
                    }
                    return 0;
                }).sum();
    }

    /**
     * 获取卡组的攻击效果
     *
     * @param cards
     * @return
     */
    public List<SkillPerformResult> attack(List<UserCard> cards) {
        List<SkillPerformResult> results = new ArrayList<>();
        //初始化攻击卡牌
        List<MaouAttackCard> attackCards = cards.stream()
                .map(MaouAttackCard::getInstance)
                .collect(Collectors.toList());

        //常规物理攻击和技能攻击buf
        attackCards.forEach(tmp -> {
            List<SkillPerformResult> skillPerformResults = getSkillPerformResult(tmp, attackCards);
            results.addAll(skillPerformResults);
        });
        //组合攻击buf
        CfgAloneMaou config = AloneMaouTool.getConfig();
        List<Integer> effectGroups = new ArrayList<>();
        attackCards.forEach(tmp -> {
            int groupId = tmp.getGroupId();
            if (config.getEffectGroups().contains(groupId) && !effectGroups.contains(groupId)) {
                effectGroups.add(groupId);
            }
        });
        effectGroups.forEach(tmp -> {
            List<SkillPerformResult> skillPerformResults = getGroupPerformResult(tmp, attackCards);
            results.addAll(skillPerformResults);
        });
        return results;
    }

    /**
     * 是否相克
     *
     * @param cardType
     * @param maouType
     * @return
     */
    private boolean isXiangKe(int cardType, int maouType) {
        if (cardType == 10 && maouType == 20) {
            return true;
        }
        if (cardType == 20 && maouType == 50) {
            return true;
        }
        if (cardType == 30 && maouType == 40) {
            return true;
        }
        if (cardType == 40 && maouType == 10) {
            return true;
        }
        if (cardType == 50 && maouType == 30) {
            return true;
        }
        return false;
    }

    /**
     * 是否属性反转
     *
     * @param cardType
     * @param maouType
     * @return
     */
    public boolean isXiangKeAsReverse(int cardType, int maouType) {
        if (cardType == 10 && maouType == 40) {
            return true;
        }
        if (cardType == 20 && maouType == 10) {
            return true;
        }
        if (cardType == 30 && maouType == 50) {
            return true;
        }
        if (cardType == 40 && maouType == 30) {
            return true;
        }
        if (cardType == 50 && maouType == 20) {
            return true;
        }
        return false;
    }

    /**
     * 某张卡牌的物理攻击和技能攻击结果
     *
     * @param attackCard
     * @param attackCards
     * @return
     */
    private List<SkillPerformResult> getSkillPerformResult(MaouAttackCard attackCard, List<MaouAttackCard> attackCards) {
        List<SkillPerformResult> results = new ArrayList<>();
        List<Integer> activedSkill = attackCard.getActiveSkill();
        if (ListUtil.isEmpty(activedSkill)) {
            return results;
        }
        activedSkill.forEach(skill -> {
            ISkillService skillService = getSkillService(skill);
            if (skillService != null) {
                SkillPerformParam param = SkillPerformParam.getInstance(attackCard, skill, attackCards);
                List<SkillPerformResult> skillResults = skillService.peform(param);
                results.addAll(skillResults);
            }
        });
        return results;
    }

    /**
     * 获得组合生效结果
     *
     * @param groupId
     * @param attackCards
     * @return
     */
    private List<SkillPerformResult> getGroupPerformResult(int groupId, List<MaouAttackCard> attackCards) {
        List<SkillPerformResult> results = new ArrayList<>();
        ISkillService skillService = getSkillService(groupId);
        if (skillService == null) {
            return results;
        }
        SkillPerformParam param = SkillPerformParam.getInstance(null, groupId, attackCards);
        List<SkillPerformResult> skillResults = skillService.peform(param);
        results.addAll(skillResults);
        return results;
    }

    /**
     * 技能逻辑服务
     *
     * @param performSkill
     * @return
     */
    private ISkillService getSkillService(int performSkill) {
        return this.skillServices.stream().filter(tmp -> tmp.isMatch(performSkill)).findFirst().orElse(null);
    }
}
