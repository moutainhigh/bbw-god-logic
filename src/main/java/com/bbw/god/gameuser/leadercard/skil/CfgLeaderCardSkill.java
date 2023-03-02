package com.bbw.god.gameuser.leadercard.skil;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CfgLeaderCardSkill implements CfgInterface {

    private List<SkillTree> skillTrees;
    //专属技能
    private List<String> exclusiveSkills;

    @Data
    public static class SkillTree{
        private Integer property;
        private Integer page;
        private Map<String,String> tree;
    }
    @Override
    public Serializable getId() {
        return "唯一";
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
