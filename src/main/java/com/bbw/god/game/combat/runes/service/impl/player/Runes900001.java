package com.bbw.god.game.combat.runes.service.impl.player;

import com.bbw.common.ListUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.fight.ZxzFightCacheService;
import com.bbw.god.game.zxz.service.ZxzCardEquipmentService;
import com.bbw.god.gameuser.card.equipment.CardEquipmentService;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 隐藏卡牌装备buff，用于处理卡牌装备buff
 *
 * @author: suhq
 * @date: 2022/9/29 10:43 上午
 */
@Service
public class Runes900001 implements IInitStageRunes {
    @Autowired
    private CardEquipmentService cardEquipmentService;
    @Autowired
    private ZxzCardEquipmentService zxzCardEquipmentService;
    @Autowired
    private CombatRedisService combatRedisService;
    @Autowired
    private ZxzFightCacheService zxzFightCacheService;

    @Override
    public int getRunesId() {
        return RunesEnum.CARD_EQUIPMENT.getRunesId();
    }

    @Override
    public void doInitRunes(CombatRunesParam param) {
        List<BattleCard> cards = param.getPerformPlayer().getDrawCards();
        if (ListUtil.isEmpty(cards)) {
            return;
        }
        //TODO: 年后统一获取战斗方式
        CombatInfo combatInfo = combatRedisService.getCombatInfo(param.getCombatId());
        if (null == combatInfo) {
            return;
        }
        Integer fightType = combatInfo.getFightType();
        if (null == fightType) {
            return;
        }
        List<Integer> cardIds = cards.stream()
                .filter(tmp -> null != tmp).map(BattleCard::getImgId)
                .collect(Collectors.toList());
        Long performUid = param.getPerformPlayer().getUid();
        List<CardFightAddition> cardEquipmentAddition;
        //诛仙阵需要特殊处理(不是获取当前)
        FightTypeEnum fightTypeEnum = FightTypeEnum.fromValue(fightType);
        switch (fightTypeEnum){
            case ZXZ:
                Integer regionId = zxzFightCacheService.getRegionId(performUid);
                cardEquipmentAddition = zxzCardEquipmentService.getEquipmentAdditions(performUid, regionId);
                break;
            case ZXZ_FOUR_SAINTS:
                Integer defenderId = zxzFightCacheService.getFourSaintsDefenderId(performUid);
                Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
                cardEquipmentAddition = zxzCardEquipmentService.getFourSaintsEquipmentAdditions(performUid, challengeType);
                break;
            default:
                cardEquipmentAddition = cardEquipmentService.getEquipmentAdditions(performUid, cardIds);
        }

        if (ListUtil.isEmpty(cardEquipmentAddition)) {
            return;
        }
        for (BattleCard card : param.getPerformPlayer().getDrawCards()) {
            if (card == null) {
                continue;
            }
            Optional<CardFightAddition> optionalAddition = cardEquipmentAddition.stream().filter(tmp -> tmp.getCardId().equals(card.getImgId())).findFirst();
            if (!optionalAddition.isPresent()) {
                continue;
            }
            CardFightAddition cardAddition = optionalAddition.get();
            handleAddition(card, cardAddition);
        }
    }

    /**
     * 处理每张卡牌的加成
     *
     * @param card
     * @param cardAddition
     */
    private void handleAddition(BattleCard card, CardFightAddition cardAddition) {
        //处理攻击加值
        card.setRoundAtk(card.getRoundAtk() + cardAddition.getAttack());
        card.setAtk(card.getAtk() + cardAddition.getAttack());
        card.setInitAtk(card.getInitAtk() + cardAddition.getAttack());
        //处理防御加值
        card.setRoundHp(card.getRoundHp() + cardAddition.getDefence());
        card.setHp(card.getHp() + cardAddition.getDefence());
        card.setInitHp(card.getInitHp() + cardAddition.getDefence());
        //添加技能
        if (ListUtil.isEmpty(cardAddition.getSkillAdditions())) {
            return;
        }
        for (CardFightAddition.SkillAddition skillAddition : cardAddition.getSkillAdditions()) {
            addSkillTOCard(getRunesId(), card, skillAddition);
        }
    }

    /**
     * 为卡牌添加技能，如果卡牌自带该技能 则将自带的替换成符文添加的
     *
     * @param runeId
     * @param targetCard
     * @param skillAddition
     */
    public void addSkillTOCard(int runeId, BattleCard targetCard, CardFightAddition.SkillAddition skillAddition) {
        int skillId = skillAddition.getSkillId();
        CfgCardSkill skill = CardSkillTool.getCardSkillOpById(skillId).get();
        List<BattleSkill> battleSkills = targetCard.getSkills().stream().filter(p -> skillId != p.getId()).collect(Collectors.toList());

        BattleSkill battleSkill = BattleSkill.instanceSkill(runeId, skill);
        battleSkill.setInitPerformProbability(skillAddition.getPerformProbability());
        battleSkill.setInitExtraRate(skillAddition.getExtraRate());
        battleSkills.add(battleSkill);

        targetCard.setSkills(battleSkills);
        targetCard.setBuff(runeId);
    }
}
