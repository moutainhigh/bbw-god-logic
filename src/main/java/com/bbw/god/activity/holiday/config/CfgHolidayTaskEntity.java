package com.bbw.god.activity.holiday.config;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 节日每日任务配置类
 * @date 2020/8/26 16:53
 **/
@Data
public class CfgHolidayTaskEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1436950051053856459L;
    private Integer id;
    private Integer type;
    private String name;
    private Integer value;
    private List<Award> awards;

    @Override
    public int getSortId() {
        return this.getId();
    }
}
