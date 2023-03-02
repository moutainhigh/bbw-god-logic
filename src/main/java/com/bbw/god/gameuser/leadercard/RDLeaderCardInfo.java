package com.bbw.god.gameuser.leadercard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 主角卡信息
 * @author liuwenbin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLeaderCardInfo implements Serializable {
    private static final long serialVersionUID = 1120820110984934072L;
    private Integer baseId = null;
    private Integer fashion = null;
    private Integer level = null;
    private Integer hierarchy = null;
    private Integer skill0;
    private Integer skill5;
    private Integer skill10;
    private Integer sex;
    private Integer star;
    private Integer property;
    private Integer atk;//总攻击
    private Integer hp;//总防御
    /** 当前使用技能组 */
    private Integer usingGroup;

    /**
     * 当前经验  已扣除等级的基础部分
     */
    private Long exp=0L;
    /**
     * 下一级需要的经验 已扣除等级的基础部分
     */
    private Long nextLvExp= 0L;


    public static RDLeaderCardInfo getInstance(UserLeaderCard leaderCard){
        RDLeaderCardInfo info=new RDLeaderCardInfo();
        info.setBaseId(leaderCard.getBaseId());
        info.setFashion(leaderCard.getFashion());
        info.setLevel(leaderCard.getLv());
        info.setStar(leaderCard.getStar());
        info.setHierarchy(leaderCard.getHv());
        info.setProperty(leaderCard.getProperty());
        info.setSex(leaderCard.getSex());
        info.setAtk(leaderCard.settleTotalAtkWithEquip());
        info.setHp(leaderCard.settleTotalHpWithEquip());
        info.setSkill0(leaderCard.currentSkills()[0]);
        info.setSkill5(leaderCard.currentSkills()[1]);
        info.setSkill10(leaderCard.currentSkills()[2]);
        long baseExp = LeaderCardTool.getLvNeedExp(leaderCard.getLv());
        info.setExp(leaderCard.getExp()-baseExp);
        info.setNextLvExp(LeaderCardTool.getLvNeedExp(leaderCard.getLv()+1)-baseExp);
        info.setUsingGroup(leaderCard.gainUsingIndex());
        return info;
    }
}
