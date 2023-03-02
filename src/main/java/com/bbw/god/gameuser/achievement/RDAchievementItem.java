package com.bbw.god.gameuser.achievement;

import com.bbw.god.rd.item.RDAchievableItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 成就返回项
 * @date 2020/11/23 11:10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAchievementItem extends RDAchievableItem implements Serializable {
    private static final long serialVersionUID = 1L;

    public RDAchievementItem(int achievementId, int status, int progress, int type) {
        CfgAchievementEntity achievement = AchievementTool.getAchievement(achievementId);
        setId(achievementId);
        setStatus(status);
        setProgress(progress);
        setType(type);
        setTotalProgress(achievement.getValue());
        setAwards(achievement.getAwards());
    }
}
