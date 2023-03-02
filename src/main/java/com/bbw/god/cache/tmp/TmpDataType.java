package com.bbw.god.cache.tmp;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.holiday.processor.HolidayGroceryShop.HolidayUserGroceryShop;
import com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision.UserChineseZodiacConllision;
import com.bbw.god.activity.holiday.processor.holidaychristmaswish.UserHolidayChristmasWish;
import com.bbw.god.activity.holiday.processor.holidaydaydoublegold.HolidayUserDayDoubleGold;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.game.GameHolidayMagicWitch;
import com.bbw.god.activity.holiday.processor.holidaymagicwitch.data.user.UserHolidayMagicWitch;
import com.bbw.god.activity.holiday.processor.holidaythankflowerlanguage.UserThankFlowerLanguage;
import com.bbw.god.activity.worldcup.entity.UserDroiyan8Info;
import com.bbw.god.activity.worldcup.entity.UserProphetInfo;
import com.bbw.god.activity.worldcup.entity.UserQuizKingInfo;
import com.bbw.god.activity.worldcup.entity.UserSuper16Info;
import com.bbw.god.gameuser.task.activitytask.UserActivityDailyTask;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 临时数据类型
 *
 * @author: suhq
 * @date: 2022/11/15 10:31 上午
 */
@Getter
@AllArgsConstructor
public enum TmpDataType {
    USER_DROIYAN_8("UserDroiyan8", UserDroiyan8Info.class),//玩家世界杯-玩家决战8强
    USER_SUPER_16("UserSuper16", UserSuper16Info.class),//玩家世界杯-超级16强
    USER_PROPHET("UserProphet", UserProphetInfo.class),//玩家世界杯-玩家我是预言家
    USER_QUIZ_KING("UserQuizKing", UserQuizKingInfo.class),//玩家世界杯-我是竞猜王
    /** 玩家活动每日任务 */
    USER_ACTIVITY_DAILY_TASK("UserActivityDailyTask", UserActivityDailyTask.class),
    /** 玩家感恩花语 */
    USER_THANK_FLOWER_LANGUAGE("UserThankFlowerLanguage", UserThankFlowerLanguage.class),
    /** 玩家杂货小铺 */
    USER_GROCERY_SHOP("UserGroceryShop", HolidayUserGroceryShop.class),
    /** 全服魔法女巫 */
    GAME_HOLIDAY_MAGICWITCH("GameHolidayMagicWitch", GameHolidayMagicWitch.class),
    /** 玩家魔法女巫 */
    USER_HOLIDAY_MAGICWITCH("UserHolidayMagicWitch", UserHolidayMagicWitch.class),
    /** 圣诞心愿 */
    USER_HOLIDAY_CHRISTMASWISH("UserHolidayChristmaswish", UserHolidayChristmasWish.class),
    /** 双倍元宝 */
    HOLIDAY_USER_DAY_DOUBLE_GOLD("HolidayUserDayDoubleGold", HolidayUserDayDoubleGold.class),
    /** 生肖对碰 */
    USER_CHINESE_ZODIAC_CONLLISION("UserChineseZodiacConllision", UserChineseZodiacConllision.class),
    ;
    //
    private String redisKey;
    private Class<? extends AbstractTmpData> entityClass;

    public static TmpDataType fromRedisKey(String key) {
        for (TmpDataType item : values()) {
            if (item.getRedisKey().equals(key)) {
                return item;
            }
        }
        throw CoderException.fatal("没有键值为[" + key + "]的数据类型！");
    }

    /**
     * 根据类对象，获取数据类型
     *
     * @param clazz
     * @return
     */
    public static <T extends AbstractTmpData> TmpDataType fromClass(Class<T> clazz) {
        for (TmpDataType item : values()) {
            if (item.getEntityClass().equals(clazz)) {
                return item;
            }
        }
        throw CoderException.fatal("没有class为[" + clazz + "]的类型！");
    }

}
