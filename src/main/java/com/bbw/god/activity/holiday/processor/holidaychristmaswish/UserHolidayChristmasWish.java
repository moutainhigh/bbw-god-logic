package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.common.ID;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.tmp.AbstractTmpData;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 圣诞心愿
 *
 * @author: huanghb
 * @date: 2022/12/14 17:31
 */
@Data
public class UserHolidayChristmasWish extends AbstractTmpData implements Serializable {
    private static final long serialVersionUID = 5466079746969473227L;
    private long gameUserId;
    /** 礼物心愿 */
    private Integer giftWish;
    /** npcId */
    private Integer npcId;
    /** 心愿任务状态 */
    private Integer status = TaskStatusEnum.DOING.getValue();


    /**
     * 构建实例
     *
     * @param uid
     * @return
     */
    public static UserHolidayChristmasWish instance(long uid) {
        List<Integer> wishGiftIds = HolidayChristmasWishTool.getGiftWishIds();
        Integer wishGiftId = PowerRandom.getRandomFromList(wishGiftIds);
        List<Integer> npcIds = HolidayChristmasWishTool.getNpcIds();
        Integer npcId = PowerRandom.getRandomFromList(npcIds);
        UserHolidayChristmasWish userHolidayChristmasWish = new UserHolidayChristmasWish();
        userHolidayChristmasWish.setId(ID.INSTANCE.nextId());
        userHolidayChristmasWish.setGameUserId(uid);
        userHolidayChristmasWish.setGiftWish(wishGiftId);
        userHolidayChristmasWish.setNpcId(npcId);
        return userHolidayChristmasWish;
    }

    public void completeWish() {
        this.status = TaskStatusEnum.ACCOMPLISHED.getValue();
        return;
    }
}
