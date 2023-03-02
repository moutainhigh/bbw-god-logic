package com.bbw.god.activity.holiday.lottery;

import com.bbw.common.ID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家节日博饼类抽奖
 * @date 2020/9/17 16:42
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserHolidayBoCake extends BaseUserHolidayLottery implements Serializable {
    private static final long serialVersionUID = 6303089055087405766L;
    // 已领取的奖品配置id集合
    private List<Integer> awardedIds = new LinkedList<>();
    // 状元编号集合
    private List<String> zhuangYuanNOList = new ArrayList<>();
    // 上次丢骰子的结果
    private List<Integer> lastResult;


    public static UserHolidayBoCake getInstance(long uid) {
        UserHolidayBoCake holidayBoCake = new UserHolidayBoCake();
        holidayBoCake.setId(ID.INSTANCE.nextId());
        holidayBoCake.setGameUserId(uid);
        return holidayBoCake;
    }

    public void receiveAward(int cfgId) {
        this.awardedIds.add(cfgId);
    }

    public void addZhuangYuanNO(String number) {
        this.zhuangYuanNOList.add(number);
    }
}
