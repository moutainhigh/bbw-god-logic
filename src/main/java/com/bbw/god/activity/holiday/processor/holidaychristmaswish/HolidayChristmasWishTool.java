package com.bbw.god.activity.holiday.processor.holidaychristmaswish;

import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 圣诞心愿参数配置工具
 *
 * @author: huanghb
 * @date: 2022/12/27 10:05
 */
public class HolidayChristmasWishTool {
    /**
     * 获得配置类
     *
     * @return
     */
    public static CfgChristmasWish getCfg() {
        return Cfg.I.getUniqueConfig(CfgChristmasWish.class);
    }

    /**
     * 获得最大心愿数量
     *
     * @return
     */
    public static Integer getMaxWishNum() {
        return getCfg().getMaxWishNum();
    }

    /**
     * 获得圣诞心愿信息
     *
     * @return
     */
    public static List<CfgChristmasWish.ChristmasWish> getChristmasWishs() {
        return getCfg().getChristmasWishs();
    }

    /**
     * 获得礼物心愿id集合
     *
     * @return
     */
    public static List<Integer> getGiftWishIds() {
        return getCfg().getChristmasWishs().stream().map(CfgChristmasWish.ChristmasWish::getGiftWish).collect(Collectors.toList());
    }

    /**
     * 获得npcid集合
     *
     * @return
     */
    public static List<Integer> getNpcIds() {
        return getCfg().getNpcIds();
    }

    /**
     * 是否同一类别
     *
     * @param wishGift
     * @param giftWish
     * @return
     */
    public static boolean isSameType(int wishGift, int giftWish) {
        List<CfgChristmasWish.ChristmasWish> christmasWishs = getChristmasWishs().stream().filter(tmp -> tmp.getGiftWish() == wishGift || tmp.getGiftWish() == giftWish).collect(Collectors.toList());
        long wishTypeNum = christmasWishs.stream().map(CfgChristmasWish.ChristmasWish::getType).distinct().count();
        return 1 == wishTypeNum;
    }

    /**
     * 获得心愿奖励
     *
     * @param wishGift
     * @param giftWish
     * @return
     */
    public static List<Award> getWishAwards(int wishGift, int giftWish) {
        //获得礼物心愿信息
        CfgChristmasWish.ChristmasWish christmasWish = getChristmasWishs().stream().filter(tmp -> tmp.getGiftWish() == giftWish).findFirst().orElse(null);
        //没有对应心愿信息
        if (null == christmasWish) {
            throw CoderException.high(String.format("没有配置礼物心愿={}的礼物", giftWish));
        }
        //送的礼物即对礼物心愿
        if (wishGift == giftWish) {
            return christmasWish.getFavoriteAwards();
        }
        //不是对应的礼物心愿，但类别一样
        boolean sameType = isSameType(wishGift, giftWish);
        if (sameType) {
            return christmasWish.getLikeAwards();
        }
        //不是对应的礼物心愿，类别也不一样
        return christmasWish.getOrdinaryAwards();
    }
}
