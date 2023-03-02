package com.bbw.god.activity.holiday.lottery;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 节日抽奖奖励配置
 * @date 2020/8/27 14:55
 **/
@Data
public class CfgHolidayLotteryAwards implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1341975239179802282L;
    private Integer id;
    private Integer prop;//概率
    private List<Award> awards;
    private Integer order;// 展示顺序
    private Integer level;
    private Integer type;
    private Integer num=1;// 该奖励的数量
    private List<Integer> accTotal;//累计次数


    @Override
    public int getSortId() {
        return this.getId();
    }
}
