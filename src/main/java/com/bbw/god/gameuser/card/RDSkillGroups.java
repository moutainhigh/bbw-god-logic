package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 技能组
 * @author: hzf
 * @create: 2022-08-30 15:59
 **/
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class RDSkillGroups extends RDSuccess implements Serializable {


    private static final long serialVersionUID = -3448387287921208292L;
    private List<RDSkillGroup> skillGroups;
    private String currentSkillGroup;

    public static RDSkillGroups getInstance(List<RDSkillGroup> skillGroups,String currentSkillGroup){
        RDSkillGroups rd = new RDSkillGroups();
        rd.setSkillGroups(skillGroups);
        rd.setCurrentSkillGroup(currentSkillGroup);
        return rd;
    }



    @Data
    public static class RDSkillGroup{
        private Integer skill0;
        private Integer skill5;
        private Integer skill10;
        private String skillGroupkey;

        /**
         * 实例技能组信息
         * @param ucSkillGroup
         * @param skillGroupKey
         * @param cfgCard
         * @return
         */
        public static RDSkillGroup setRDSkillGroup(UserCard.SkillGroup ucSkillGroup,String skillGroupKey, CfgCardEntity cfgCard) {
            RDSkillGroup rdSkillGroup = new RDSkillGroup();
            rdSkillGroup.setSkillGroupkey(skillGroupKey);
            rdSkillGroup.setSkill0(ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_0) == 0 ? cfgCard.getZeroSkill() : ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_0));
            rdSkillGroup.setSkill5(ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_5) == 0 ? cfgCard.getFiveSkill()  : ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_5));
            rdSkillGroup.setSkill10(ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_10) == 0 ? cfgCard.getTenSkill() : ucSkillGroup.gainSkill(CardSkillPosEnum.SKILL_10));
            return rdSkillGroup;
        }
    }

}
