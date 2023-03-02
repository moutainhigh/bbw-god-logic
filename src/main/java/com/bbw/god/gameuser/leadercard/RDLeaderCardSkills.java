package com.bbw.god.gameuser.leadercard;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/**
 * 法外分身技能组
 *
 * @author fzj
 * @date 2021/10/15 17:14
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLeaderCardSkills extends RDSuccess {
    /** 技能组 */
    private UserLeaderCardSkills skillsGroupInfo;
}
