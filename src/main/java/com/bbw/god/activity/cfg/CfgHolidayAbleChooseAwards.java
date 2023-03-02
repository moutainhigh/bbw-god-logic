package com.bbw.god.activity.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 节日可选择奖励
 *
 * @author: huanghb
 * @date: 2021/12/31 13:44
 */
@Data
public class CfgHolidayAbleChooseAwards implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    /** 活动Id */
    private Integer id;
    /** 活动类别 */
    private Integer type;
    /** 可选择奖励 */
    private List<Award> awards;

    @Override
    public int getSortId() {
        return id;
    }
}
