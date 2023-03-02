package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 气流词条 我方卡牌在云台位上时无法使用任何技能。
 * 当我方卡牌处于云台位时，该卡牌上的所有技能都不会进行触发。
 *
 * @author longwh
 * @date 2023/1/5 15:32
 */
@Service
public class Runes333302 implements IRoundStageRunes {
    @Autowired
    private BattleCardService battleCardService;

    @Override
    public int getRunesId() {
        return RunesEnum.QI_LIU_ENTRY.getRunesId();
    }

    @Override
    public Action doRoundRunes(CombatRunesParam param) {
        Action action = new Action();
        Player player = param.getPerformPlayer();
        // 获取符文发动方云台卡牌
        int yunTaiPos = PositionService.getYunTaiPos(player.getId());
        Optional<BattleCard> yunTaiCard = battleCardService.getCard(player, yunTaiPos);
        if (!yunTaiCard.isPresent()) {
            return action;
        }
        for (BattleSkill skill : yunTaiCard.get().getSkills()) {
            // 禁用技能一个回合
            skill.getTimesLimit().forbidOneRound(getRunesId());
        }
        // 触发 补充一个动画
        AnimationSequence amin = ClientAnimationService.getSkillAction(param.getNextSeq(), getRunesId(), param.getMyPlayerPos());
        action.addClientAction(amin);
        return action;
    }
}