package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuwenbin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLeaderCardSkillTree extends RDCommon {

    /**
     * 上场技能
     */
    private List<SkillState> deploySkills = null;
    /**
     * 法术技能
     */
    private List<SkillState> magicSkill = null;
    /**
     * 攻击技能
     */
    private List<SkillState> attackSkill = null;
    /**
     * 死亡技能
     */
    private List<SkillState> dieSkill = null;
    /**
     * 防御技能
     */
    private List<SkillState> defenseSkill = null;
    /**
     * 特殊技能
     */
    private List<SkillState> otherSkill = null;


    private List<SkillState> skillTree = null;
    private Integer page=null;
    private Integer property=null;

    /**
     * 初始化需要分类显示技能的实例
     * @return
     */
    public static RDLeaderCardSkillTree getShowSkillInstance(){
        RDLeaderCardSkillTree rd=new RDLeaderCardSkillTree();
        rd.setDeploySkills(new ArrayList<>());
        rd.setAttackSkill(new ArrayList<>());
        rd.setDieSkill(new ArrayList<>());
        rd.setDefenseSkill(new ArrayList<>());
        rd.setOtherSkill(new ArrayList<>());
        return rd;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SkillState implements Serializable {
        /**
         * 技能ID
         */
        private int skillId;
        /**
         * 状态 : -1 未激活 1可激活 2已激活
         */
        private Integer state = null;
        /**
         * 需要点亮前置节点数
         */
        private Integer preNodes = null;
        /**
         * 已经点亮的节点数
         */
        private Integer activeNodes = null;
        /**
         * 需要消耗的技能卷轴Id
         */
        private Integer scrollId = null;

        public static SkillState getInstance(int skillId){
            SkillState skillState=new SkillState();
            skillState.setSkillId(skillId);
            return skillState;
        }

        /**
         * 已激活
         * @param skillId
         * @return
         */
        public static SkillState getActiveInstance(int skillId){
            return new SkillState(skillId,SkillStateEnum.ACTIVATED.getState(), null,null,null);
        }

        public static SkillState getInstance(int skillId,SkillStateEnum skillStateEnum,int preNodes,int activeNodes,Integer scrollId){
            return new SkillState(skillId,skillStateEnum.getState(), preNodes,activeNodes,scrollId);
        }

        public boolean hasActive(){
            return state!=null && SkillStateEnum.ACTIVATED.getState()==state;
        }
    }

}
