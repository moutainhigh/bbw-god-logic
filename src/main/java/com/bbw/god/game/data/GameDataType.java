package com.bbw.god.game.data;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.game.GameActivity;
import com.bbw.god.activityrank.GameRankAwardRecord;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaBooth;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaMarketBargain;
import com.bbw.god.game.dfdj.store.DfdjZoneMallRecord;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.flx.FlxDayResult;
import com.bbw.god.game.limit.GameBlackIps;
import com.bbw.god.game.sxdh.SxdhZone;
import com.bbw.god.game.sxdh.store.SxdhZoneMallRecord;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import com.bbw.god.gameuser.businessgang.digfortreasure.GameDigTreasure;
import com.bbw.god.mall.lottery.GameLottery;
import com.bbw.god.mall.snatchtreasure.GameSnatchTreasureCard;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.special.GameSpecialPrice;
import com.bbw.god.game.zxz.entity.ZxzInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务器数据类型
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-03 14:43
 */
@Getter
@AllArgsConstructor
public enum GameDataType {
    FLXRESULT("flxresult", FlxDayResult.class), // 福临轩开奖结果信息
    ACTIVITY("activity", GameActivity.class), // 活动信息
    ACTIVITY_RANK("activityRank", GameActivityRank.class), // 跨服冲榜信息
    ACTIVITY_RANK_AWARD_RECORD("activityRankAward", GameRankAwardRecord.class), // 跨服奖励发放记录
    SXDH_ZONE("sxdhZone", SxdhZone.class), // 神仙大会战区信息
    SXDH_ZONE_MALL_RECORD("sxdhZoneMallRecord", SxdhZoneMallRecord.class),
    DFDJ_ZONE("dfdjZone", DfdjZone.class), // 巅峰对决战区信息
    DFDJ_ZONE_MALL_RECORD("dfdjZoneMallRecord", DfdjZoneMallRecord.class),
    LOTTERY("lottery", GameLottery.class),// 奖券
    SNATCH_TREASURE_CARD("snatchTreasureCard", GameSnatchTreasureCard.class),// 夺宝卡牌
    GAME_SPECIAL_PRICE("gameSpecialPrice", GameSpecialPrice.class),// 全服合成特产价格
    GAME_FST("fstRanking", FstGameRanking.class),//跨服封神台
    TRANSMIGRATION("transmigration", GameTransmigration.class),//轮回世界
    BLACK_IPS("blackIps", GameBlackIps.class),//黑名单ip
    /** 女娲集市 */
    NV_WA_MARKET("nvWaMarket", GameNvWaBooth.class),
    /** 女娲集市讨价信息 */
    NV_WA_MARKET_BARGAIN("nvWaMarketBargain", GameNvWaMarketBargain.class),
    /** 诛仙阵 */
    ZXZ("zxz", ZxzInfo.class),
    /** 诛仙阵 ：四圣挑战 */
    ZXZ_FOUR_SAINTS("zxzFourSaintsInfo", ZxzFourSaintsInfo.class),
    /** 挖宝 */
    GAME_DIG_TREASURE("gameDigTreasure", GameDigTreasure.class),
    ;
    //
    private String redisKey;
    private Class<? extends GameData> entityClass;

    public static GameDataType fromRedisKey(String key) {
        for (GameDataType item : values()) {
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
    public static <T extends GameData> GameDataType fromClass(Class<T> clazz) {
        for (GameDataType item : values()) {
            if (item.getEntityClass().equals(clazz)) {
                return item;
            }
        }
        throw CoderException.fatal("没有class为[" + clazz + "]的类型！");
    }

}
