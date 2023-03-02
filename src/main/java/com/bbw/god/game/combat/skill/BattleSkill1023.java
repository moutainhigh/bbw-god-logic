package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatCardTools;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardFactory;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 蝗征 1023：上场时，召唤与施法者等级阶数相同的螽斯，填满我方阵位。且每回合，都将召唤螽斯填满我方阵位。召唤的卡牌拥有与持有卡牌相同的技能且从场上离开时消失
 * 自带【隐藏蝗征 3175】
 *
 * @author longwh
 * @date 2022/12/27 11:03
 */
@Service
public class BattleSkill1023 extends BattleSkillService {

    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private UserCardService userCardService;

    @Override
    public int getMySkillId() {
        return CombatSkillEnum.HUANG_ZHENG.getValue();
    }

    @Override
    protected Action attack(PerformSkillParam psp) {
        Action action = new Action();
        // 获取螽斯配置信息
        Optional<Integer> haveFx = CardTool.getCardById(CardEnum.ZHONG_SI.getCardId()).getSkills().stream().filter(id ->
                id == CombatSkillEnum.FX.getValue()).findFirst();
        boolean hasFeiXSkill = haveFx.isPresent();
        // 获取当前的空余阵位（根据飞行技能判断是否包含云台）
        Integer[] emptyPosArr = ArrayUtils.toObject(psp.getPerformPlayer().getEmptyBattlePos(hasFeiXSkill));
        if (emptyPosArr.length == 0){
            // 无空余阵位 不触发技能
            return action;
        }
        // 执行【蝗征 1023】
        action.addClientAction(ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos()));
        for (Integer toPos : emptyPosArr) {
            // 召唤卡牌
            doSummonCard(action, psp, toPos);
        }
        // 添加被动技能
        addPassiveSkills(psp);
        return action;
    }

    /**
     * 添加被动技能
     *
     * @param psp
     */
    private void addPassiveSkills(PerformSkillParam psp){
        Optional<CfgCardSkill> cardSkillOpById = CardSkillTool.getCardSkillOpById(CombatSkillEnum.YIN_CANG_HUANG_ZHENG.getValue());
        if (!cardSkillOpById.isPresent()){
            return;
        }
        BattleSkill passiveSkill = BattleSkill.instanceSkill(getMySkillId(), cardSkillOpById.get());
        // 首次附加不释放
        passiveSkill.getTimesLimit().forbidOneRound(getMySkillId());
        psp.getPerformCard().addSkill(passiveSkill);
    }

    /**
     * 召唤卡牌
     *
     * @param action
     * @param psp
     * @param toPos
     * @return
     */
    private BattleCard doSummonCard(Action action, PerformSkillParam psp, int toPos) {
        Player player = psp.getPerformPlayer();
        BattleCard performCard = psp.getPerformCard();
        // 生成卡牌
        CfgCardEntity cfgCard = CardTool.getHideCard(CardEnum.ZHONG_SI.getCardId());
        BattleCard card = BattleCardFactory.buildCard(cfgCard, performCard.getLv(), performCard.getHv());
        updateCardSkills(psp.getPerformPlayer().getUid(), card);
        runesPerformService.runInitCardRunes(psp.getPerformPlayer(), card);

        if (psp.getCombat().getWxType() != null){
            // 如果战斗类型为 背水赛，则移除卡牌技能：复活、回魂、封神
            if (psp.getCombat().getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
                card.getSkills().removeIf(p ->
                        p.getId() == CombatSkillEnum.FH.getValue() ||
                        p.getId() == CombatSkillEnum.HH.getValue() ||
                        p.getId() == CombatSkillEnum.FS.getValue()
                );
            }
        }
        card.setPos(toPos);
        //初始化卡
        battleCardService.replaceCard(player, card);
        AnimationSequence as = new AnimationSequence(psp.getNextAnimationSeq(), Effect.EffectResultType.CARD_ADD);
        AnimationSequence.Animation animation = new AnimationSequence.Animation();
        animation.setPos1(performCard.getPos());
        animation.setPos2(toPos);
        animation.setSkill(getMySkillId());
        animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
        as.add(animation);
        action.addClientAction(as);
        return card;
    }

    /**
     * 更新为玩家持有的该卡技能
     *
     * @param performUid
     * @param sumonCard
     */
    private void updateCardSkills(long performUid, BattleCard sumonCard) {
        boolean isPerformAi = performUid <= 0;
        if (isPerformAi) {
            return;
        }
        UserCard userCard = userCardService.getUserCard(performUid, sumonCard.getImgId());
        BattleCardFactory.updateCardSkills(sumonCard, userCard);
    }
}