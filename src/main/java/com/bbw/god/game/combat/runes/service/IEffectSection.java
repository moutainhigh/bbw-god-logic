package com.bbw.god.game.combat.runes.service;

import java.util.List;

/**
 * 效果执行阶段
 *
 * @author longwh
 * @date 2023/2/27 15:35
 */
public interface IEffectSection {

    /**
     * 获取阶段
     * @return
     */
    Integer getSection();

    /**
     * 获取当前阶段所有技能id
     *
     * @return
     */
    List<Integer> getSectionSkillIds();

    /**
     * 校验技能
     *
     * @return
     */
    boolean next(int skillId);
}