package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 镜甲词条 我方召唤师将受到与敌方召唤师等同的负面上场、回合技能效果。
 * 负面效果为：召唤师的血量/法力值减少，手牌上限减少。
 *
 * @author longwh
 * @date 2023/1/4 15:48
 */
@Service
public class Runes333208 implements IRoundStageRunes, IRoundEndStageRunes {
    @Override
    public int getRunesId() {
        return RunesEnum.JING_JIA_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        List<Effect> effectList = new ArrayList<>();
        // 召唤师血量变动时发动的符文
        for (Effect effect : param.getReceiveEffect()) {
            // 不为上场、回合技能效果 不处理
            if (!check(effect.getPerformSkillID())) {
                continue;
            }
            // 效果目标 非敌方玩家 不处理
            if (!param.isEffectToEnemy() || !PositionService.isZhaoHuanShiPos(effect.getTargetPos())) {
                continue;
            }
            if (!effect.isValueEffect()){
                continue;
            }
            CardValueEffect valueEffect = effect.toValueEffect();
            // 处理法力值扣除的效果
            int effectMp = valueEffect.getRoundMp() + valueEffect.getMp();
            if (effectMp < 0) {
                // 防止多次处理（上场、回合技能）的效果
                if (param.getTargetCard() == null) {
                    continue;
                }
                // 我方 受到敌方 相同的法力值减少效果
                Effect cloneEffect = CloneUtil.clone(effect);
                cloneEffect.setSourceID(getRunesId());
                cloneEffect.setPerformSkillID(getRunesId());
                cloneEffect.setTargetPos(param.getMyPlayerPos());
                effectList.add(cloneEffect);
                continue;
            }
            int effectHp = valueEffect.getRoundHp() + valueEffect.getHp();
            // 非伤害 不处理
            if (effectHp >= 0){
                continue;
            }
            // 我方 受到敌方 相同的血量减少效果
            Effect cloneEffect = CloneUtil.clone(effect);
            cloneEffect.setSourceID(getRunesId());
            cloneEffect.setPerformSkillID(getRunesId());
            cloneEffect.setTargetPos(param.getMyPlayerPos());
            effectList.add(cloneEffect);
        }
        if (ListUtil.isNotEmpty(effectList)) {
            action.addEffects(effectList);
        }
        return action;
    }
    @Override
    public Action doRoundEndRunes(CombatRunesParam param) {
        // 回合结束初始化前发动
        Action action = new Action();
        for (BattleCard myCard : param.getPerformPlayer().getPlayingCards()) {
            if (myCard == null) {
                continue;
            }
            // 偷营技能
            Optional<BattleSkill> tySkill = myCard.getSkill(CombatSkillEnum.TOU_YING.getValue());
            if (tySkill.isPresent()) {
                // 同步敌方手牌上限减少 状态到 我方
                int handCardLimit= param.getOppoPlayer().getStatistics().getHandCardUpLimit();
                param.getPerformPlayer().getStatistics().setHandCardUpLimit(handCardLimit);
            }
            // 蛊惑技能
            Optional<BattleSkill> ghSkill = myCard.getSkill(CombatSkillEnum.GH.getValue());
            if (ghSkill.isPresent()) {
                // 同步敌方法力值减益 状态到 我方
                int handCardRoundMpAddtion = param.getOppoPlayer().getStatistics().getHandCardRoundMpAddtion();
                int mp = param.getOppoPlayer().getStatistics().getMp();
                param.getPerformPlayer().getStatistics().setHandCardRoundMpAddtion(handCardRoundMpAddtion);
                param.getPerformPlayer().getStatistics().setMp(mp);
            }
            if (tySkill.isPresent() || ghSkill.isPresent()) {
                //触发 补充一个动画
                AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), param.getMyPlayerPos());
                action.addClientAction(amin);
            }
        }
        return action;

    }

    /**
     * 检查上场、回合技能效果
     *
     * @param skillId
     * @return
     */
    private boolean check(int skillId) {
        // 上场技能
        SkillSection deploySection = SkillSection.getDeploySection();
        // 攻击技能
        SkillSection skillSection = SkillSection.getSkillAttackSection();
        return deploySection.contains(skillId) || skillSection.contains(skillId);
    }
}