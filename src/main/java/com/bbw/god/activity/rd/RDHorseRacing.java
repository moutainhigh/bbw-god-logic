package com.bbw.god.activity.rd;

import com.bbw.god.rd.item.RDItems;
import lombok.Data;

import java.io.Serializable;

/**
 * @author：lwb
 * @date: 2021/3/10 10:23
 * @version: 1.0
 */
@Data
public class RDHorseRacing extends RDItems<RDActivityItem> implements Serializable {
    private Integer horseRacingPoint;//赛马积分
    private int[] betInfo;//投注信息
    private Long settleTime;//开赛倒计时
    private Integer stopBet=0;
}
