package com.bbw.god.gameuser.achievement;

import com.bbw.god.rd.item.RDItems;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 获取成就列表
 *
 * @author suhq
 * @date 2019年3月12日 下午5:24:54
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDAchievementList extends RDItems<RDAchievementItem> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer finishNums;
    private Integer finishScore;
    @Deprecated
    private List<RDAchievement> achievements = null;// 所有成就列表（旧），客户端统一后删除

    public static RDAchievementList getInstance(Integer finishNums, Integer finishScore, List<RDAchievement> achievements) {
        RDAchievementList rdAchievementList = new RDAchievementList();
        rdAchievementList.setFinishNums(finishNums);
        rdAchievementList.setFinishScore(finishScore);
        rdAchievementList.setAchievements(achievements);
        return rdAchievementList;
    }

    public static RDAchievementList getInstance(List<RDAchievementItem> achievementItems, Integer finishNums, Integer finishScore) {
        RDAchievementList rdAchievementList = new RDAchievementList();
        rdAchievementList.setFinishNums(finishNums);
        rdAchievementList.setFinishScore(finishScore);
        return rdAchievementList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Deprecated
    public static class RDAchievement implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id = null;
        private Integer status = null;
        private Integer progress = null;
        private Integer type = null;

        public RDAchievement(int achievementId, int status, int progress, int type) {
            this.id = achievementId;
            this.status = status;
            this.progress = progress;
            this.type = type;
        }
    }

}
