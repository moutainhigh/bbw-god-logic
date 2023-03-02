package com.bbw.god.activity.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 元宵节祈福天灯活动配置
 *
 * @author fzj
 * @date 2022/2/9 9:11
 */
@Data
public class CfgPrayerSkyLanternConfig implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 放飞天灯目标及奖励 */
    private List<SkyLanternTarget> targetWithAwards;

    @Data
    public static class SkyLanternTarget {
        /** 天灯次数 */
        private Integer skyLanternTimes;
        /** 奖励 */
        private List<Award> awards;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
