package com.bbw.god.activity.holiday.lottery;

import com.bbw.common.ID;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 类型=30的玩家节日抽奖
 * @date 2020/12/22 14:49
 **/
@Data
//@EqualsAndHashCode(callSuper = true)
public class UserHolidayLottery30 extends BaseUserHolidayLottery implements Serializable {
    private static final long serialVersionUID = -8767590270861668523L;
    /** 珠子排序记录 */
    private List<DrawResult> records = new ArrayList<>();


    public static UserHolidayLottery30 getInstance(long uid) {
        UserHolidayLottery30 instance = new UserHolidayLottery30();
        instance.setGameUserId(uid);
        instance.setId(ID.INSTANCE.nextId());
        return instance;
    }

    /**
     * 添加记录
     *
     * @param record 本次珠子的排序
     */
    public void addRecord(List<Integer> record) {
        this.records.add(DrawResult.getInstance(record));
    }

}
