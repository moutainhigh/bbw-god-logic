package com.bbw.god.activity.holiday.config;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 节日拜访村庄事件奖励
 *
 * @author: huanghb
 * @date: 2022/2/9 15:34
 */
@Data
public class CfgHolidayCunZEventAwardEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /** 节日活动类别 */
    private Integer activityType;
    /** 概率 */
    private Integer prob;
    /** 奖励 */
    private List<Award> awards;

    /**
     * 获取配置类
     *
     * @return
     */
    public static List<CfgHolidayCunZEventAwardEntity> getCfg(int holidayType) {
        List<CfgHolidayCunZEventAwardEntity> cfgAwards = Cfg.I.get(CfgHolidayCunZEventAwardEntity.class);
        return cfgAwards.stream().filter(tmp -> tmp.getActivityType() == holidayType).collect(Collectors.toList());
    }

    /**
     * 根据概率随机获得奖励
     *
     * @param holidayType
     * @return
     */
    public static List<Award> randomAwardByProb(int holidayType) {
        List<CfgHolidayCunZEventAwardEntity> cfgAwards = getCfg(holidayType);
        List<Integer> probs = cfgAwards.stream().map(CfgHolidayCunZEventAwardEntity::getProb).collect(Collectors.toList());
        Integer awardIndex = PowerRandom.hitProbabilityIndex(probs);
        return cfgAwards.get(awardIndex).getAwards();
    }

    @Override
    public int getSortId() {
        return this.getId();
    }
}
