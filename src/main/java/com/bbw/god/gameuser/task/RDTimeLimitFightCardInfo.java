package com.bbw.god.gameuser.task;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.task.timelimit.TimeLimitFightCardPool;
import com.bbw.god.gameuser.task.timelimit.TimeLimitFightTaskService;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 村庄疑云战斗任务卡组信息
 *
 * @author fzj
 * @date 2022/1/6 13:40
 */
@Data
public class RDTimeLimitFightCardInfo extends RDCommon implements Serializable {
    private static TimeLimitFightTaskService timeLimitFightTaskService = SpringContextUtil.getBean(TimeLimitFightTaskService.class);
    private static final long serialVersionUID = 1L;
    /** 对手卡组 */
    private List<RDFightsInfo.RDFightCard> opponentCards;
    /** 我方卡组 */
    private List<RDFightsInfo.RDFightCard> ownCards;
    /** 可选卡池 */
    private List<RDFightsInfo.RDFightCard> optionalCardsPool;

    public static List<RDFightsInfo.RDFightCard> setCardGroup(List<CCardParam> params) {
        if (params.isEmpty()){
            return new ArrayList<>();
        }
        return params.stream().map(f -> {
            RDFightsInfo.RDFightCard rdCard = new RDFightsInfo.RDFightCard();
            rdCard.setBaseId(f.getId());
            rdCard.setLevel(f.getLv());
            rdCard.setHierarchy(f.getHv());
            rdCard.setSkill0(f.getSkills().get(0));
            rdCard.setSkill5(f.getSkills().get(1));
            rdCard.setSkill10(f.getSkills().get(2));
            boolean useSkillScroll = false;
            List<Integer> originalSkills = CardTool.getCardById(f.getId()).getSkills();
            for (int i = 0; i < f.getSkills().size(); i++) {
                if (f.getSkills().get(i).intValue() != originalSkills.get(i)) {
                    useSkillScroll = true;
                    break;
                }
            }
            rdCard.setIsUseSkillScroll(useSkillScroll ? 1 : 0);
            return rdCard;
        }).collect(Collectors.toList());
    }

    public static List<RDFightsInfo.RDFightCard> setCardGroup(RDFightsInfo rdFightsInfo) {
        if (rdFightsInfo.getCards().isEmpty()){
            return new ArrayList<>();
        }
        return rdFightsInfo.getCards().stream().peek(f -> {
            boolean useSkillScroll = false;
            List<Integer> originalSkills = CardTool.getCardById(f.getBaseId()).getSkills();
            TimeLimitFightCardPool fightCard = timeLimitFightTaskService.getOrCreateRandomCardsSkills()
                    .stream().filter(s -> s.getCardId().equals(f.getBaseId())).findFirst().orElse(null);
            if (null == fightCard){
                return;
            }
            f.setSkill0(fightCard.getSkills().get(0));
            f.setSkill5(fightCard.getSkills().get(1));
            f.setSkill10(fightCard.getSkills().get(2));
            for (int i = 0; i < fightCard.getSkills().size(); i++) {
                if (fightCard.getSkills().get(i).intValue() != originalSkills.get(i)) {
                    useSkillScroll = true;
                    break;
                }
            }
            f.setIsUseSkillScroll(useSkillScroll ? 1 : 0);
        }).collect(Collectors.toList());
    }
}

