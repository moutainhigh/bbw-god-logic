package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IInitStageRunes;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.ZxzRegionDefender;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.fight.ZxzFightCacheService;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.game.zxz.service.ZxzCardEquipmentService;
import com.bbw.god.game.zxz.service.ZxzEnemyService;
import com.bbw.god.game.zxz.service.foursaints.GameZxzFourSaintsService;
import com.bbw.god.gameuser.card.equipment.cfg.CardFightAddition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 灵装词条：敌方卡牌装备[X]级仙决并装备至宝
 * @author: hzf
 * @create: 2022-12-15 11:05
 **/
@Service
public class Runes331209 implements IInitStageRunes {
    @Autowired
    private ZxzCardEquipmentService zxzCardEquipmentService;
    @Autowired
    private CombatRedisService combatRedisService;
    @Autowired
    private ZxzFightCacheService zxzFightCacheService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;
    @Autowired
    private GameZxzFourSaintsService gameZxzFourSaintsService;

    @Override
    public int getRunesId() {
        return RunesEnum.LING_ZHUANG_ENTRY.getRunesId();
    }
    @Override
    public void doInitRunes(CombatRunesParam param) {
        CombatBuff combatBuff = param.getPerformPlayer().gainBuff(getRunesId());
        int lv = combatBuff.getLevel();
        Long performUid = param.getPerformPlayer().getUid();
        List<CardFightAddition> zxzCardEquipmentAddition = new ArrayList<>();
        //TODO: 年后统一获取战斗方式
        CombatInfo combatInfo = combatRedisService.getCombatInfo(param.getCombatId());
        if (null == combatInfo) {
            return;
        }

        Integer fightType = combatInfo.getFightType();
        if (null == fightType) {
            return;
        }
        FightTypeEnum fightTypeEnum = FightTypeEnum.fromValue(fightType);
        switch (fightTypeEnum){
            case ZXZ:
                //获取关卡id
                Integer defenderId = zxzFightCacheService.getDefenderId(performUid);
                //得到关卡信息
                ZxzRegionDefender zxzRegionDefender = zxzEnemyService.getZxzRegionDefender(defenderId);
                zxzCardEquipmentAddition = handleCardFightAddition(lv, zxzRegionDefender.getCardXianJues(), zxzRegionDefender.getCardZhiBaos(), zxzRegionDefender.getDefenderCards());
                break;
            case ZXZ_FOUR_SAINTS:
                //获取关卡id
                Integer fourSaintsDefenderId = zxzFightCacheService.getFourSaintsDefenderId(performUid);
                Integer challengeType = CfgFourSaintsTool.getChallengeType(fourSaintsDefenderId);
                //获取关卡信息
                ZxzFourSaintsDefender zxzFourSaintsDefender = gameZxzFourSaintsService.getZxzFourSaintsDefender(challengeType, fourSaintsDefenderId);
                zxzCardEquipmentAddition = handleCardFightAddition(lv, zxzFourSaintsDefender.getCardXianJues(), zxzFourSaintsDefender.getCardZhiBaos(), zxzFourSaintsDefender.getDefenderCards());
                break;
            default:
                return;
        }

        if (ListUtil.isEmpty(zxzCardEquipmentAddition)) {
            return;
        }
        //处理敌方的卡牌装备加成
        for (BattleCard drawCard : param.getOppoPlayer().getDrawCards()) {
            if (drawCard == null) {
                continue;
            }
            Optional<CardFightAddition> optionalAddition = zxzCardEquipmentAddition.stream().filter(tmp -> tmp.getCardId().equals(drawCard.getImgId())).findFirst();
            if (!optionalAddition.isPresent()) {
                continue;
            }
            CardFightAddition cardAddition = optionalAddition.get();
            handleAddition(drawCard, cardAddition);
        }

    }
    private List<CardFightAddition> handleCardFightAddition(Integer lv,List<String> xianJues,List<String> zhiBaos,List<String> DefenderCards){
        //获取灵装词条加成后的仙决
        List<CardXianJueRandomRule> cardXianJueRandomRules = ZxzAnalysisService.gainCardXianJues(xianJues);
        List<CardXianJueRandomRule> cardXianJues = ZxzAnalysisService.instanceCardXianJueByEntryLv(lv, cardXianJueRandomRules);
        //获取灵装词条加成后的至宝
        List<CardZhiBaoRandomRule> cardZhiBaoRandomRules = ZxzAnalysisService.gainCardZhiBaos(zhiBaos);
        List<CardZhiBaoRandomRule> cardZhiBaos = ZxzAnalysisService.instanceCardZhiBaoByEntryLv(lv,cardZhiBaoRandomRules);
        //获取这个关卡的卡牌id
        List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(DefenderCards);
        List<Integer> cardIds = zxzCards.stream().map(ZxzCard::getCardId).collect(Collectors.toList());
        //获得加成
        List<CardFightAddition> zxzCardEquipmentAddition = zxzCardEquipmentService.getEquipmentAdditions(cardXianJues, cardZhiBaos, cardIds);
        return zxzCardEquipmentAddition;
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
