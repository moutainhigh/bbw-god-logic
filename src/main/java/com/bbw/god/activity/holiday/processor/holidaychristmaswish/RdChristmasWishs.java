package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 圣诞心愿活动
 *
 * @author: huanghb
 * @date: 2022/12/14 9:43
 */
@Data
public class RdChristmasWishs extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /*圣诞心愿*/
    private List<rdWishInfo> wishInfos;

    @Data
    public static class rdWishInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 唯一标识 */
        private long id;
        /*心愿礼物*/
        private Integer wishGift;
        /*npcId*/
        private Integer npcId;

    }

    /**
     * 初始化
     *
     * @param userHolidayChristmasWishs
     * @return
     */
    public static RdChristmasWishs instance(List<UserHolidayChristmasWish> userHolidayChristmasWishs) {
        RdChristmasWishs rdChristmasWishs = new RdChristmasWishs();
        List<rdWishInfo> rdWishInfoList = new ArrayList<>();
        for (UserHolidayChristmasWish userHolidayChristmasWish : userHolidayChristmasWishs) {
            rdWishInfo rdWishInfo = new rdWishInfo();
            rdWishInfo.setId(userHolidayChristmasWish.getId());
            rdWishInfo.setWishGift(userHolidayChristmasWish.getGiftWish());
            rdWishInfo.setNpcId(userHolidayChristmasWish.getNpcId());
            rdWishInfoList.add(rdWishInfo);
        }
        rdChristmasWishs.setWishInfos(rdWishInfoList);
        return rdChristmasWishs;
    }
}
