package com.bbw.god.server;

import com.bbw.exception.CoderException;
import com.bbw.god.activity.server.ServerActivity;
import com.bbw.god.activityrank.RankAwardRecord;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.detail.FightDetail;
import com.bbw.god.server.flx.FlxCaiShuZiBet;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import com.bbw.god.server.flx.ServerFlxResult;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.FstRobot;
import com.bbw.god.server.god.ServerGod;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.maou.ServerMaou;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.auction.ServerMaouAuction;
import com.bbw.god.server.monster.ServerMonster;
import com.bbw.god.server.msg.ServerBroadcast;
import com.bbw.god.server.special.ServerSpecialCityPrice;
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
public enum ServerDataType {
    INFO("info", ServerInfo.class, 1), // 服务器信息
    STATISTIC("statistic", ServerStatistic.class, 2),
    ACTIVITY("activity", ServerActivity.class, 21), // 活动信息
    ACTIVITY_RANK("activityRank", ServerActivityRank.class, 22),
    ACTIVITY_RANK_AWARD_RECORD("activityRankAward", RankAwardRecord.class, 23),
    GOD("god", ServerGod.class, 3), // 神仙信息
    MONSTER("monster", ServerMonster.class, 4), // 野怪信息
    MAOU("maou", ServerMaou.class, 5), // 魔王信息
    ALONE_MAOU("maouAlone", ServerAloneMaou.class, 61), // 魔王信息
    BOSS_MAOU("maouBoss", ServerBossMaou.class, 62), // 魔王信息
    SPECIAL_PRICE("specialPrice", ServerSpecialCityPrice.class, 6),
    FLXCAISHUZI("flxCaiShuZi", FlxCaiShuZiBet.class, 31), // 福临轩数字投注馆
    FLXYAYALE("flxYaYaLe", FlxYaYaLeBet.class, 32), // 福临轩元素馆投注馆
    FLXRESULT("flxResult", ServerFlxResult.class, 33), // 福临轩
    FSTPVPRanking("fstRanking", FstRanking.class, 7), // 封神台排行
    broadcast("broadcast", ServerBroadcast.class, 8), // 区服广播
    DETAIL_FIGHT("fightDetail", FightDetail.class, 9), // 战斗明细
    Guild_Info("guildInfo", GuildInfo.class, 41),//行会信息
    FST_ROBOT("fstRobot", FstRobot.class, 51),
    MAOU_AUCTION("maouAuction", ServerMaouAuction.class, 100),// 拍卖
    ;
    //
    private final String redisKey;
    private final Class<? extends ServerData> entityClass;
    private final int typeId;

    public static ServerDataType fromRedisKey(String key) {
        for (ServerDataType item : values()) {
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
     * @param <T>
     * @return
     */
    public static <T extends ServerData> ServerDataType fromClass(Class<T> clazz) {
        for (ServerDataType item : values()) {
            if (item.getEntityClass().equals(clazz)) {
                return item;
            }
        }
        throw CoderException.fatal("没有class为[" + clazz + "]的类型！");
    }

}
