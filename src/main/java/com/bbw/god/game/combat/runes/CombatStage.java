package com.bbw.god.game.combat.runes;

import lombok.Data;

/**
 * @author：lwb
 * @date: 2020/12/8 14:54
 * @version: 1.0
 */
@Data
public class CombatStage {
    private String key;
    private int[] skillIds;
    public CombatStage(String key,int[] skills){
        this.key=key;
        this.skillIds=skills;
    }

    /**
     * 是否存在该技能ID
     * @param skillId
     * @return
     */
    public boolean exist(int skillId){
        for (int id : skillIds) {
            if (id==skillId){
                return true;
            }
        }
        return false;
    }
}
