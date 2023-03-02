package com.bbw.god.activity.holiday.processor.holidaydaydoublegold;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.cache.tmp.AbstractTmpData;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家元宝首充双倍数据
 *
 * @author: huanghb
 * @date: 2023/1/5 9:47
 */
@Data
public class HolidayUserDayDoubleGold extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 玩家id */
    private long gameUserId;
    /** 购买次数 */
    private Integer buyNum = 0;
    /** 生成时间 */
    private Date generateTime = DateUtil.now();

    /**
     * 添加够买次数
     */
    public void addBuyNum() {
        buyNum++;
    }


    /**
     * 构建实例
     *
     * @param uid
     * @return
     */
    public static HolidayUserDayDoubleGold instance(long uid) {
        HolidayUserDayDoubleGold holidayUserGroceryShop = new HolidayUserDayDoubleGold();
        holidayUserGroceryShop.setId(ID.INSTANCE.nextId());
        holidayUserGroceryShop.setGameUserId(uid);
        return holidayUserGroceryShop;
    }
}
