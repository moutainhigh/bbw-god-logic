package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CardExpTool;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;

/**
 * 切换技能组返回的信息
 * @author: hzf
 * @create: 2022-08-31 14:59
 **/
@Data
public class RDChangeSkillGroups extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -3448387287921208292L;
    private Integer skill0;
    private Integer skill5;
    private Integer skill10;
    /** 是否使用技能卷轴 */
    private Integer isUseSkillScroll;
    /** 使用技能卷轴的次数 */
    private Integer usst = 0;

    public static  RDChangeSkillGroups getInstance(UserCard uc){
        RDChangeSkillGroups rd = new RDChangeSkillGroups();
        rd.setSkill0(uc.gainSkill0());
        rd.setSkill5(uc.gainSkill5());
        rd.setSkill10(uc.gainSkill10());
        int isUseSkillScroll = uc.ifUseSkillScroll() ? 1 : 0;
        rd.setIsUseSkillScroll(isUseSkillScroll);
        int usst = null == uc.getStrengthenInfo() ? 0 : uc.getStrengthenInfo().gainUseSkillScrollTimes();
        rd.setUsst(usst);
        return rd;
    }

}
