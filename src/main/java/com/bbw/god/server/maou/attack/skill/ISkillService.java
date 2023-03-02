package com.bbw.god.server.maou.attack.skill;

import java.util.List;

/**
 * @author suhq
 * @description: 技能效果
 * @date 2019-12-31 13:24
 **/
public interface ISkillService {

    List<SkillPerformResult> peform(SkillPerformParam param);

    int getPerformId();

    default boolean isMatch(int skillId) {
        return getPerformId() == skillId;
    }
}
