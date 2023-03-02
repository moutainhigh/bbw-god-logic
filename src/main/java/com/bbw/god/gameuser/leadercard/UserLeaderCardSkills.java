package com.bbw.god.gameuser.leadercard;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 法外分身技能组
 *
 * @author fzj
 * @date 2021/10/15 15:03
 */
@Data
public class UserLeaderCardSkills{
    /** 当前使用技能组 */
    private Integer usingIndex =0;
    /** 技能组 */
    private List<int[]> skillsGroupInfo = new ArrayList<>();

    /**
     * 获取当前技能组数量
     * @return
     */
    public int getSkillsGroupNum(){
        return skillsGroupInfo.size();
    }
}
