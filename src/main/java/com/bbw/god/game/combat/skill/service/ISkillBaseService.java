package com.bbw.god.game.combat.skill.service;

import org.springframework.stereotype.Service;

/**
 * @author lwb
 */
@Service
public interface ISkillBaseService {
    /**
     * 当前技能ID
     *
     * @return
     */
    int getMySkillId();
    /**
     * 是否是 技能id对应的服务
     *
     * @param skillId
     * @return
     */
    default boolean match(int skillId) {
        return getMySkillId() == skillId;
    }

    default int getInt(Double b) {
        return b.intValue();
    }

    default boolean contains(int[] values, int value) {
        if (null == values || values.length == 0) {
            return false;
        }
        for (int match : values) {
            if (match == value) {
                return true;
            }
        }
        return false;
    }
}
